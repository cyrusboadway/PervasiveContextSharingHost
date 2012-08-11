# Framework for Context Aware Pervasive Computing

This code base provides a framework for the sharing of Contextual data which characterises an environment, to
facilitate Pervasive Computing in ad-hoc networked environments. It is the basis of my Master's thisis.

## Outline

### Context Sharing Paradigm

There are three roles for context sharing, Context Providers, Context Consumers, and Context Hosts.

#### Context Providers

Context Providers are devices which have information that characterises the environment. They make this
information accessible to others, sometimes in a restricted fashion.

Examples of Context Providers might be:

- Ambient lights, willing to indicate the current light level.
- A cellphone, sharing it's current ring status.
- A home theatre, broadcasting it's media play status.

#### Context Consumers

Context consumers are software which access contextual data and adapt themselves accordingly, to better
perform their assigned duties.

For example:

- A desktop application might look for ringing phones belonging to the currently logged-in user, and display the caller-id in a desktop notification.
- The ambient lights might dim when a home theatre in the same location begins to play. 

#### Context Hosts

Context Hosts accept context data from Providers and make it available to Consumers and other Hosts. A
Consumer can query a Host to answer a question about the environment. The Host is responsible for respecting
the access restrictions designated by the Provider. 

### Context Schema

The contextual data is expressed in XML. Primitives and basic Entity models are outlined in a set of XML
Schema documents, which can be extended for any particular purpose. Context Consumers can query the
data using XML processing languages like xpath, xQuery, or even XSLT.

Example context data sets are available at /xml/ device1.xml persone1.xml testEntity.xml, all of which
conform with the schema outlined in the thesis accompanying this source.

## Running the Code

### Dependencies

- _Bluetooth_: [Bluecove](http://bluecove.org)
- _IP network decentralized service discover_: [jmDNS](http://jmdns.sourceforge.net/) v3.4 
- _For XML_:
    - [BaseX](basex.org) v6.1
    - [Apache Saxon HE](http://saxon.sourceforge.net/)

### Running Actors

### Hosts

There are two supported network fabrics.

- IP network host: ca/queensu/pervasive/socketserver SocketServer.java
- Bluetooth host: ca/queensu/pervasive/bluetoothserver BluetoothServer.java

### Providers

An example of a context provider adding data to a host repository can be seen: /test/ RegisterClientTest.java

### Consumer

An example of a context consumer requesting data from a host can be seen: /test/ testQuerySystem.java


## More information

More information, including a copy of my thesis, are available by request: [Cyrus Boadway](mailto:cyrus@boadway.ca)
