declare namespace host="http://cs.queensu.ca/Host";
declare namespace base="http://cs.queensu.ca/ContextBase";

let $expiry := /base:Entity/xs:integer(@base:expiry)

return
if(count($expiry)>0) then
	if($expiry>ENTITY_EXPIRY) then
		"valid"
	else
		"expired"	
else
	"valid"