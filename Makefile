clean: 
	docker compose down
	docker image rm back-test | echo ""

test: clean
	docker compose run --rm test

build: test
	docker compose up --rm

db:
	docker compose run db

build_target_build:
	docker build --target build -t build_target_build .

run_target_build: build_target_build
	docker run -v ~/.m2:/root/.m2 --rm build_back
