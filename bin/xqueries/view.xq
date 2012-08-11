declare namespace host="http://cs.queensu.ca/Host";
declare namespace base="http://cs.queensu.ca/ContextBase";

declare function local:view-entity($entity as element(),$element as element()) as element() { 
            element {node-name($element)} 
            {$element/@*, 
                for $child in $element/node()
                where (not(string($child/@private)="true")
                or $entity/base:PermissionGroups/base:PermissionGroup[base:ContextID/string()=$child/@contextID/string()][base:EntityID/string()="REPLACE"]
                or $entity[@ID/string() = "REPLACE"])
                return
                    if ($child instance of element())
                    then local:view-entity($entity,$child)
                    else $child
            }
        };


for $host in /host:Host
return <host:Host xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
{$host/@ID}
{
for $entities in $host/*
return local:view-entity($entities,$entities)
}
</host:Host>