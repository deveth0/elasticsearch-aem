# ElasticSearch AEM Integration

This bundle provides a Replication Agent that can be used to index AEM Pages / DAM Assets on activation. Furthermore some helper to perform a search are implemented.

## Installation

You can build this bundle from source using Maven:

    mvn clean install

To install it into an [Apache Sling](http://sling.apache.org/) Environment (or AEM), use the sling:install command:

    mvn sling:install -Dsling.url=http://localhost:PORT

For the initial installation, you should use the `complete` project which bundles all dependencies into a single artifact.

# Usage

## Setup
After a successful installation, visit the [System Configuration](http://localhost:4502/system/console/configMgr) and setup a `ElasticSearch Search Provider`.

NOTE: Currently no authentication is supported.

The next step is to [setup a Replication Agent](http://localhost:4502/miscadmin#/etc/replication/agents.author) in `/etc/replication/agents.author`. Although the edit mode provides numberous options, currently only `Enabled` and `Serialization Type` are used.

To enable the Agent, click the `Enabled` checkbox, select `Elastic Search Index Content` as `Serialization Type` and enter any url (e.g. `http://foo`).

Now you are ready and can test the Connection. If everything works as expected, you should now see the default response from your ElasticSearch installation.

    31.12.2016 15:33:02 - { "name" : "JhCSfm7", "cluster_name" : "elasticsearch", "cluster_uuid" : "eaCZcjuYRn-sTRZFKn-eIg", "version" : { "number" : "5.1.1", "build_hash" : "5395e21", "build_date" : "2016-12-06T12:36:15.409Z", "build_snapshot" : false, "lucene_version" : "6.3.0" }, "tagline" : "You Know, for Search" }

## Add template to ElasticSearch

You can find a Template for ElasticSearch Mappings in `/misc/template_aem.json`. It includes some settings required to filter search results by path or template.

To install it, either use your favorite tool (e.g. Kibana) or curl:

    curl -X POST -d @misc/template_aem.json http://localhost:9200/_template/aem

## Index custom fields

For each entry the path is index. In addion the following fields are indexed by default:

cq:Page
* jcr:title
* jcr:description
* cq:template
* cq:lastModified

dam:Asset
* dc:title
* dc:description
* jcr:lastModified

You can configure additional fields by creating a `ElasticSearch Index Configuration` in [System Configuration](http://localhost:4502/system/console/configMgr).


# Restrictions

Currently the following essential features are missing:

* No Authentication is supported
* The used Index is hardcoded (`idx`)
* Only one ElasticSearch Host is supported
* The mapping has to be installed manually
* No Reindexing Support
* Configure Mapping in `ElasticSearch Index Configuration`
