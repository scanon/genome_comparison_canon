KB_TOP ?= /kb/dev_container
KB_RUNTIME ?= /kb/runtime
DEPLOY_RUNTIME ?= $(KB_RUNTIME)
TARGET ?= /kb/deployment
CURR_DIR = $(shell pwd)
SERVICE_NAME = $(shell basename $(CURR_DIR))
SERVICE_DIR = $(TARGET)/services/$(SERVICE_NAME)
LIB_JARS_DIR = $(TARGET)/lib/jars
TARGET_PORT = 8283
THREADPOOL_SIZE = 5

default: compile

deploy: distrib

deploy-all: distrib

test:
	./run_tests.sh $(LIB_JARS_DIR)

compile: src
	./make_war.sh $(SERVICE_DIR) $(LIB_JARS_DIR)
	mkdir -p ./clients
	compile_typespec GenomeComparison.spec ./clients
	find ./clients -type f | grep -v Client | xargs rm

distrib:
	@echo "Service folder: $(SERVICE_DIR)"
	mkdir -p $(SERVICE_DIR)
	cp -f ./dist/service.war $(SERVICE_DIR)
	cp -f ./glassfish_start_service.sh $(SERVICE_DIR)
	cp -f ./glassfish_stop_service.sh $(SERVICE_DIR)
	echo "./glassfish_start_service.sh $(SERVICE_DIR)/service.war $(TARGET_PORT) $(THREADPOOL_SIZE)" > $(SERVICE_DIR)/start_service.sh
	chmod +x $(SERVICE_DIR)/start_service.sh
	echo "./glassfish_stop_service.sh $(TARGET_PORT)" > $(SERVICE_DIR)/stop_service.sh
	chmod +x $(SERVICE_DIR)/stop_service.sh
	./create_config.sh $(SERVICE_DIR) $(THREADPOOL_SIZE)

clean:
	@echo "nothing to clean"
