build-all:
	./gradlew build
	docker build -t order-service ./marketplace-order-service
	docker build -t inventory-service ./marketplace-inventory-service
	docker build -t notification-service ./marketplace-notification-service
	docker build -t payment-service ./marketplace-payment-service
	docker build -t pricing-service ./marketplace-pricing-service
	docker build -t user-service ./marketplace-user-service