FROM openjdk:11

ARG username
ARG uid
ARG gid

# create the current user inside the docker image
RUN groupadd -g ${gid} ${username}
RUN useradd -m -s /bin/bash ${username} -u ${uid} -g ${gid}

WORKDIR /opt/build

CMD tail -f /dev/null
