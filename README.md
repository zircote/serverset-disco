Zookeeper ServerSet Discovery Plugin for Elasticsearch
======================================================





In order to install the plugin, run: 

```sh
bin/plugin install zircote/serverset-plugin/1.0.0-snapshot
```

You need to install a version matching your Elasticsearch version:

|       Elasticsearch    |  AWS Cloud Plugin |                                                             Docs                                                                   |
|------------------------|-------------------|------------------------------------------------------------------------------------------------------------------------------------|
|    es-1.5              | 1.0.0-SNAPSHOT    | [1.0.0-snapshot](https://github.com/zircote/serverset-plugin)                                                                      |

To build a `SNAPSHOT` version, you need to build it with Maven:

```bash
mvn clean install
plugin --install serverset --url file:target/releases/serverset-plugin-1.0-SNAPSHOT.zip
```

## Generic Configuration

 
```

```
## Discovery

Here is a simple sample configuration:

```
discovery:
  type: serverset
  serverset_path: /aurora/ec2-user/prod/elasticsearch
  zk_ensemble: 127.0.0.1:2181
    
```



License
-------

    This software is licensed under the Apache 2 license, quoted below.

    Copyright 2015 Robert Allen <zircote@gmail.com>

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.