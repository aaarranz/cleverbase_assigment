FROM postgres:12

# Install OpenJDK-13
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

RUN apt-get update \
    && apt-get install -y --no-install-recommends tzdata curl ca-certificates fontconfig locales \
    && echo "en_US.UTF-8 UTF-8" >> /etc/locale.gen \
    && locale-gen en_US.UTF-8 \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_VERSION jdk-11.0.9.1+1

RUN set -eux; \
    ARCH="$(dpkg --print-architecture)"; \
    case "${ARCH}" in \
       aarch64|arm64) \
         ESUM='e9cea040cdf5d9b0a2986feaf87662e1aef68e876f4d66664cb2be36e26db412'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jdk_aarch64_linux_hotspot_11.0.9.1_1.tar.gz'; \
         ;; \
       armhf|armv7l) \
         ESUM='871618e96c57ef348fa068ffebf7e935c29c8601d59790a0d08dfd0d5c6f8d66'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jdk_arm_linux_hotspot_11.0.9.1_1.tar.gz'; \
         ;; \
       ppc64el|ppc64le) \
         ESUM='d94b6b46a14ab0974b1c1b89661741126d8cf8a0068b471b8f5fa286a71636b1'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jdk_ppc64le_linux_hotspot_11.0.9.1_1.tar.gz'; \
         ;; \
       s390x) \
         ESUM='65cc100cc353d77c237f28b24323b647805d30267dcd6505ab7fdb538c16da49'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jdk_s390x_linux_hotspot_11.0.9.1_1.tar.gz'; \
         ;; \
       amd64|x86_64) \
         ESUM='e388fd7f3f2503856d0b04fde6e151cbaa91a1df3bcebf1deddfc3729d677ca3'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.9.1%2B1/OpenJDK11U-jdk_x64_linux_hotspot_11.0.9.1_1.tar.gz'; \
         ;; \
       *) \
         echo "Unsupported arch: ${ARCH}"; \
         exit 1; \
         ;; \
    esac; \
    curl -LfsSo /tmp/openjdk.tar.gz ${BINARY_URL}; \
    echo "${ESUM} */tmp/openjdk.tar.gz" | sha256sum -c -; \
    mkdir -p /opt/java/openjdk; \
    cd /opt/java/openjdk; \
    tar -xf /tmp/openjdk.tar.gz --strip-components=1; \
    rm -rf /tmp/openjdk.tar.gz;

ENV JAVA_HOME=/opt/java/openjdk \
    PATH="/opt/java/openjdk/bin:$PATH"

COPY target/scala-2.11/cleverbase-assigment-assembly-1.0.jar /cleverbase-assigment-assembly-1.0.jar
COPY init-rest-server.sh /docker-entrypoint-initdb.d/init-rest-server.sh
COPY init-data-script.sh /docker-entrypoint-initdb.d/init-data-script.sh

RUN chmod uo+x /docker-entrypoint-initdb.d/init-data-script.sh
RUN chmod uo+x /docker-entrypoint-initdb.d/init-rest-server.sh
EXPOSE 9090