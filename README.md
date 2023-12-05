INSTRUCTIONS:

1. Install Jackson databind, annotations, and core
2. Install Apache POI
3. Paste your API Key as an environment variable like so:
   OPENAI_API_KEY = YOUR_API_KEY_HERE
4. Create a json file titled "presentation.json" structured as follows:
 
   {

   "audience": Paste desired audience here (age range, etc.),

   "topic": Paste the presentation topic here,

   "starting_content": Paste any starting research you have here. This can be a paper or whatever you have available ,

   "slide_count": Paste the amount of slides you want here.
   
    }


    *Please note that all fields are strings.*
5. You may need to edit line 23 in Presentation.java if you are experiencing errors, as your file structure may be different.
6. If everything is formatted correctly, you should have a working powerpoint.
