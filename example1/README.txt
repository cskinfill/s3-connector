CMIS DEMO
=========

HOW TO DEMO:
  1.run one time the "cmisDemo" flow. Probably won't detect any change but it will save the changelog token.
    a.  you can verify this on the mongodb: 
         > db.cmisdemo.find();
         { "_id" : ObjectId("4d61a69eecd91674c2d5718f"), "repository" : "371554cd-ac06-40ba-98b8-e6b60275cca7", "changelogToken" : "42802" }
  2. Lunch the OpenCMIS Workbench http://cmis.alfresco.com/opencmis/workbench.jnlp and log in.
  3. Create a folder, a .txt document, and other type of file
  4. Run "cmisDemo" flow
  5. you will see see some debug information in the console, and two tweets in the account. The one of the txt file will contain an abstract of the content.

DISCLAMER:
   - OpenCMIS client changelog function (or alfresco server) aren't always returning the last changelog token (to be investigated). 
     That's why we use a idempotent-filter to filter change events already seen. Sadly the filter is not working.

HOW IT WORKS:
   - CMIS connector provides a changelog operation that can be called with a "last change token" to indicate the last change seen.
   - Use MongoDB to store the "last change token" for the next call
   - For each Change Event, we keep only Documents that are currently accesible
       - Fetch metadata of each document
       - Create a message to publish in twitter using the metadata
           - if the document is text/plain we add to the message an abstract
             (it would be great to do the same with plain/html using jtidy + xslt)
           - Hydratate a URI to the document content and shorten it with bitly
       - publish the message link twitter

All this is implemented in several flows. Those that are network independent
are tested in junit test.

WHAT HAS NOT BEEN DEMO:
    - The ability of creating documents and folders on a CMIS repository.
    
