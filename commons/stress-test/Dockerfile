FROM debian:jessie

# Set the reset cache variable
ENV REFRESHED_AT 2015-06-15

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update -y &&\
    apt-get install -y git make build-essential libssl-dev unzip net-tools curl lua5.1 liblua5.1-dev

WORKDIR /tmp

# Install wrk - benchmarking software
# Resource: https://github.com/wg/wrk/wiki/Installing-Wrk-on-Linux

RUN git clone https://github.com/wg/wrk.git &&\
    cd wrk &&\
    make &&\
    mv wrk /usr/local/bin


# Install Luarocks - a lua package manager
# Raise the limits to successfully run benchmarks

RUN curl http://keplerproject.github.io/luarocks/releases/luarocks-2.2.2.tar.gz -O &&\
    tar -xzvf luarocks-2.2.2.tar.gz &&\
    cd luarocks-2.2.2 &&\
    ./configure &&\
    make build &&\
    make install &&\
    luarocks install lua-cjson &&\
    luarocks install uuid &&\
    ulimit -c -m -s -t unlimited

WORKDIR /

ENV DEBIAN_FRONTEND=newt