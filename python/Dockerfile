FROM python:3.7

ARG username
ARG uid
ARG gid

# create the current user inside the docker image
RUN groupadd -g ${gid} ${username}
RUN useradd -m -s /bin/bash ${username} -u ${uid} -g ${gid}

# add .local/bin in user's home directory to path for installed ties scripts
ENV PATH="${PATH}:/home/${username}/.local/bin"

WORKDIR /opt/build
COPY src/requirements.txt ./
COPY src/requirements-build.txt ./

RUN pip install -r requirements.txt
RUN pip install -r requirements-build.txt

CMD tail -f /dev/null
