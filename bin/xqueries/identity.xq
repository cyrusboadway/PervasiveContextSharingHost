declare function local:view-entity($element as element()) as element() { 
            element {node-name($element)} 
            {$element/@*, 
                for $child in $element/node()
                return
                    if ($child instance of element())
                    then local:view-entity($child)
                    else $child
            }
        };

for $host in /*
return local:view-entity($host)