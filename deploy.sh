#!/bin/bash

echo "🚀 Starting SalonHub API Production Deployment..."

# Build the application
echo "📦 Building application..."
./gradlew clean bootJar

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "❌ Build failed! Please fix the errors and try again."
    exit 1
fi

echo "✅ Build successful!"

# Build Docker image
echo "🐳 Building Docker image..."
docker build -t salon-hub-api:latest .

if [ $? -ne 0 ]; then
    echo "❌ Docker build failed!"
    exit 1
fi

echo "✅ Docker image built successfully!"

# Test the Docker image locally (optional)
echo "🧪 Testing Docker image locally..."
docker run --rm -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_PASSWORD=test \
  -e JWT_SECRET=test-secret-key-for-local-testing-only \
  --name salon-hub-test \
  salon-hub-api:latest

# Wait a bit for the container to start
sleep 10

# Check if container is running
if docker ps | grep -q salon-hub-test; then
    echo "✅ Container is running!"
    docker stop salon-hub-test
else
    echo "❌ Container failed to start. Check logs:"
    docker logs salon-hub-test
    docker rm salon-hub-test
    exit 1
fi

echo "🎉 Production build ready for deployment!"
echo "📋 Next steps:"
echo "   1. Push your code to GitHub"
echo "   2. Connect your Render service to your GitHub repository"
echo "   3. Set environment variables in Render:"
echo "      - SPRING_PROFILES_ACTIVE=prod"
echo "      - DB_PASSWORD=<your-postgres-password>"
echo "      - JWT_SECRET=<secure-random-string>"
echo "   4. Deploy!"
