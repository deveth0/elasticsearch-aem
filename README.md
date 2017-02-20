# ElasticSearch-AEM

This repository provides an integration of ElasticSearch into AEM.

Please see my [blog](https://www.dev-eth0.de/blog/2017/01/18/elasticsearch-aem.html) for a detailed introduction and more information.

## Requirements

The ElasticSearch AEM Integration has been tested for AEM >6.1. All required dependencies are included in the `complete` package.

To build this project, you first have to build the [elasticsearch-rest-osgi](https://github.com/deveth0/elasticsearch-rest-osgi) client.


## Start ElasticSearch using Vagrant

If you don't have a running ElasticSearch instance, you can use the provided VagrantFile in `/misc/vagrant`to setup one using Virtualbox.

````
vagrant plugin install vagrant-vbguest
vagrant up
````

This will create a local box running Debian and ElasticSearch 5.2.1. The vm listens on the private only IP 192.168.50.10, so ElasticSearch can be reached on [192.168.50.10:9200](http://192.168.50.10:9200)

