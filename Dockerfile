FROM clojure:temurin-17-tools-deps-bullseye-slim
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY deps.edn /usr/src/app
COPY build.clj /usr/src/app
RUN clj -T:build build/clean && clj -T:build build/jar
COPY . /usr/src/app
CMD [ "clj", "-X:portal" ]

