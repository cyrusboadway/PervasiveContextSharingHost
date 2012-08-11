declare namespace base="http://cs.queensu.ca/ContextBase";
declare namespace person="http://cs.queensu.ca/PersonBase";
declare namespace device="http://cs.queensu.ca/DeviceBase";

for $userid in //base:Entity[person:Name/person:Last/string()="Anderson"]/@id
return
for $email in //base:Entity[base:Relationship[@verb="ownership"][base:ObjectElement=$userid]]//device:EmailAddress
return $email/string()