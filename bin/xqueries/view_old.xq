declare function local:view-entity($entity as element(),$element as element()) as element() { 
			element {node-name($element)} 
			{$element/@*, 
				for $child in $element/node()
				where (not(string($child/@public)="false")
				or $entity/permissions/permission[@IDREF/string()=$child/@ID/string()][accessor/@IDREF/string()="REPLACE"]
				or $entity[@ID/string() = "REPLACE"])
				return
					if ($child instance of element())
					then local:view-entity($entity,$child)
					else $child
			}
		};

for $host in root()/host
return <situation><host>
{$host/@ID}
{$host/key}
{
for $entities in $host/*
return local:view-entity($entities,$entities)}
</host></situation>