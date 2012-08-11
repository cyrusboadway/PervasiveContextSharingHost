(:This query finds the public key of the first client matching the given client name. :)
for $client in root()//permissions/permission/accessor
where $client/@IDREF="REPLACE"
return $client[1]/key/string()