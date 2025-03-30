package org.tradingbot.common.strategy;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;
import org.tradingbot.common.bot.period.OpenMarketTrading;
import org.tradingbot.common.bot.period.TradingPeriod;
import org.tradingbot.common.bot.period.TradingPeriodEnum;
import org.tradingbot.common.persistence.StrategyEntity;
import org.tradingbot.common.persistence.StrategyParameterEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * https://school.stockcharts.com/doku.php?id=trading_strategies
 */
public class Strategy {

    private static final Logger LOG = LoggerFactory.getLogger(Strategy.class);
    private final String xml;
    private final String name;
    private final Map<String, BigDecimal> defaultParameters;
    private final TradingPeriod tradingPeriod;

    public Strategy(StrategyEntity strategyEntity) {
        this(
                strategyEntity.getName(),
                strategyEntity.getXml(),
                getParameters(strategyEntity.getDefaultParameters()),
                TradingPeriodEnum.getFromId(strategyEntity.getTradingPeriod())
                        .create(strategyEntity.getTradingPeriodParameters()));
    }
    private static Map<String, BigDecimal> getParameters(Collection<StrategyParameterEntity> parameters) {
        Map<String, BigDecimal> m = new HashMap<>();
        for (var parameter : parameters) {
            m.put(parameter.getName(), parameter.getDefaultValue());
        }
        return m;
    }

    public Strategy(String name, String xml, Map<String, BigDecimal> defaultParameters, TradingPeriod tradingPeriod) {
        this.name = Objects.requireNonNull(name);
        this.xml = Objects.requireNonNull(xml);
        this.defaultParameters = defaultParameters;
        this.tradingPeriod = tradingPeriod;
    }

    public Strategy(String name, String xml, Map<String, BigDecimal> defaultParameters) {
        this.name = Objects.requireNonNull(name);
        this.xml = Objects.requireNonNull(xml);
        this.defaultParameters = defaultParameters;
        this.tradingPeriod = new OpenMarketTrading();
    }

    public Strategy(String name, String xml) {
        this(name, xml, Collections.emptyMap());
    }

    public org.ta4j.core.Strategy toT4JStrategy(BarSeries series, Map<String, BigDecimal> parameters)
            throws StrategyInitException {
        Map<String, BigDecimal> params = new HashMap<>(defaultParameters);
        params.putAll(requireNonNull(parameters));
        return internalToT4JStrategy(requireNonNull(series), params);
    }

    public void testStrategy(BarSeries series) throws StrategyInitException {
        toT4JStrategy(series, Collections.emptyMap());
    }

    public org.ta4j.core.Strategy internalToT4JStrategy(BarSeries series, Map<String, BigDecimal> parameters)
            throws StrategyInitException {
        String transformedXml = xml;
        for (Map.Entry<String, BigDecimal> entry : parameters.entrySet()) {
            transformedXml =
                    StringUtils.replace(transformedXml, "${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }

        var dbf = DocumentBuilderFactory.newInstance();
        try (var inputStream = new ByteArrayInputStream(transformedXml.getBytes(StandardCharsets.UTF_8))) {
            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(inputStream);
            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            if (!doc.getDocumentElement().getNodeName().equals("strategy")) {
                throw new StrategyInitException("root name is not strategy");
            }
            Rule buyingRule = null;
            Rule sellingRule = null;
            NodeList childNodes = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                var currentNode = childNodes.item(i);
                if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (currentNode.getNodeName().equals("entryRule")) {
                        buyingRule = convertToRule(currentNode, series);
                    } else if (currentNode.getNodeName().equals("exitRule")) {
                        sellingRule = convertToRule(currentNode, series);
                    } else {
                        throw new StrategyInitException("Unknown tag " + currentNode.getNodeName());
                    }
                }
            }

            if (buyingRule == null) {
                throw new StrategyInitException("Tag buyingRule  not found");
            }

            if (sellingRule == null) {
                throw new StrategyInitException("Tag sellingRule  not found");
            }

            return new BaseStrategy(buyingRule, sellingRule);
        } catch (ClassNotFoundException | IOException | ParserConfigurationException | SAXException e) {
            throw new StrategyInitException("Error converting to strategy", e);
        }
    }

    public TradingPeriod getTradingPeriod() {
        return tradingPeriod;
    }

    private Rule convertToRule(Node rulesNode, BarSeries series) throws ClassNotFoundException, StrategyInitException {
        var childNodes = rulesNode.getChildNodes();
        Rule rule = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            var currentNode = childNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                if (rule != null) {
                    throw new StrategyInitException("Error format");
                }
                rule = (Rule) convertToObject(currentNode, series);
            }
        }
        if (rule == null) {
            throw new StrategyInitException("No rule found");
        }
        return rule;
    }

    private Object convertToObject(Node node, BarSeries series) throws StrategyInitException {
        var childNodes = node.getChildNodes();
        List<Object> parameters = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            var currentNode = childNodes.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                parameters.add(convertToObject(currentNode, series));
            }
        }
        Class<?> clazz;
        try {
            clazz = getaClass(node);
        } catch (ClassNotFoundException e) {
            // the tag is the name of the variable
            switch (node.getNodeName()) {
                case "barCount":
                case "multiplier":
                    clazz = Integer.class;
                    break;
                case "threshold":
                case "gainPercentage":
                case "lossPercentage":
                case "aNum":
                    clazz = Num.class;
                    break;
                case "minStrength":
                case "minSlope":
                    clazz = Double.class;
                    break;
                default:
                    throw new IllegalStateException("Error with node " + node.getNodeName());
            }
        }
        if (clazz.equals(Integer.class)) {
            return new BigDecimal(childNodes.item(0).getNodeValue()).toBigIntegerExact().intValue();
        }
        if (clazz.equals(Double.class)) {
            return Double.valueOf(childNodes.item(0).getNodeValue());
        }
        if (clazz.equals(Num.class)) {
            return DecimalNum.valueOf(childNodes.item(0).getNodeValue());
        }

        if (clazz.isEnum()) {
            for (var v : clazz.getEnumConstants()) {
                if (v.toString().equals(childNodes.item(0).getNodeValue()))  {
                    return v;
                }
            }
            throw new IllegalStateException("Error with " + childNodes.item(0).getNodeValue());
        }
        Constructor<?>[] constructors = clazz.getConstructors();
        Object constructor1 = createObject(parameters, constructors);
        if (constructor1 != null) {
            return constructor1;
        }

        parameters.add(0, series);
        Object constructor = createObject(parameters, constructors);
        if (constructor != null) {
            return constructor;
        }

        throw new StrategyInitException("Did not succeed to convert");
    }

    @NotNull
    private Class<?> getaClass(Node node) throws ClassNotFoundException {
        try {
            return Class.forName(node.getNodeName());
        } catch (ClassNotFoundException e) {
            var nodeName = node.getNodeName();
            var lastIndex = nodeName.lastIndexOf('.');
            if (lastIndex != -1) {
                StringBuilder sb = new StringBuilder(nodeName);
                sb.replace(lastIndex, lastIndex + 1, "$");
                nodeName = sb.toString();
                return Class.forName(nodeName);
            }
            throw e;
        }
    }

    public String getName() {
        return name;
    }

    private Object createObject(List<Object> parameters, Constructor<?>[] constructors) {
        for (var constructor : constructors) {
            try {
                // TODO refaire sans try catch
                return constructor.newInstance(parameters.toArray());
            } catch (Exception e) {
                LOG.trace("", e);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Strategy{" + "xml='" + xml + '\'' + ", name='" + name + '\'' + ", defaultParameters=" +
                defaultParameters + '}';
    }
}
