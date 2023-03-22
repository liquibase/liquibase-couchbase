-- Create collection 1
CREATE
COLLECTION /* this thing should be ignored as it is comment */ `testBucket`.sqlScope.testCol1;
-- Create collection 2
CREATE
COLLECTION /* also
    we
    support
    multiline comments and badly
    formatted couchbase sql
    */
    `testBucket`.sqlScope.testCol2;