#
# **NOTES**

1. Whenever creating/updating an intent, creating/updating for utter,entity and customer journey has to be done as its linked
2. Whenever creating/updating an entity, creating/updating of ent2syn.json is done
3. Anything done wrt synonyms or variations leads to dataset triggers from handlers,(dump data to yaml files for training)

# **VIEWSETS**

_VIEWSETS__-_ViewSets are just a type of class based view but it do not provide request method handlers like "get()", "post()", "patch()", "delete()", etc. But, it provides actions such as "create()", "list()", "retrieve()", "update()", "partial\_update()" and "destroy()".

# INTENTS.PY

_viewsets/intents.py_

BASIC WORKFLOW WHEN YOU CALL THE API FOR CREATION

1. Clone github
2. Make updates/create data on database
3. Make updates in files
4. Push data to github

CLASS INTENTSMODELVIEWSET

- Derived from ModelViewSet
- Uses IntentsSerialiser class
- Store all objects in queryset variable

METHODS

1. update\_vcs
This function is used for writing intents and its cj\_refence to intent\_metadata

- Check id vcs\_path exists , if it does check if intent\_metadata.json file path exists
- If the above is True the store json data of intent\_metadata.json in a new dictionary
- Try to get\_object() , if not then raise Assertion error and and get intent object for defined project from database
- Convert the above intent model to dictionary snd store in the new dictionary under then intent name key
- Check if customer journey\_reference key has value ,if it does store it in the new dictionary under then intent name key under "cj\_refence." key
- Write the dictionary in json form in the file intent\_metadata.json

1. Create

This function is used for creating/updating entities,cj referencr,utters and variation in database ,json and yaml

- Get the project oblect having tenant=the tenant value we get from the COOKIES data in the request
- Get intent name from the request
- If name is not in camel case raise APIException , Intent name shouls be in camel case
- Get Bb-Access-Token from request
- Get utter\_name list by splitting the camel case name obltained
- Create VCSutils object , VCSUtils constructor is called which basically clones the repository for defined project and token
- update\_or\_create\_entities is called function explained already
- push\_files is called function explained already
- Call Djangos create function for the request and get list of object with the defined project and name with help of get\_queryset and update cj\_reference for that
- Call **perform\_vcs,**  **which checks if vcs is there otherxise it cretes VCSUtils**
- **Parse through all language defined in the project**
- **Create an empty dictionary intent\_variations**
- **Check if name is there in intent\_variations dictionary, if it is not there than create a key value pair name , empty dictionary respectivley**
- Simillarly done with language for the intent\_variations[name]
- Generate variations for defined name and language and store in intent\_variations[name][lang] by calling generate\_system\_variations
- generate\_system\_variations
  - This creates variations for the language defined in the project with the help of pre defined vartion and entities obtained from intent using permutations
- create\_model\_variations
- Call create\_model\_variations
- create\_model\_variations
  - Variation is created and added to database
- Call push\_vari\_yaml\_json
- push\_vari\_yaml\_json
  - Calls generate\_variations\_yaml
  - generate\_variations\_yaml
    - Create dataset directory
    - Read and store json data to file)vari and deep copy to variations variable
    - Parse through the file with key value pair, intent and variation dictionary respectively
    - Parse variation dictionary with key value pair ,language and variation list respectively
    - If intent attribute is not there in variation file then create intent attribute in the file
    - If language attribute is not there in variation file then create language attribute in the file
    - Loop through variation list
    - Remove variation from the variations[intent][lang] list
    - Preprocess the variation by calling preprocess\_text,basically preprocesses it in correct format using syn2ent file
    - If the above preprocessed text is not there in variations[intent][lang], append it
    - Finally dump it to a yaml file so that it can be used for training
    - Preprocessor will help generate corpus file from synonyms.yaml
    - Push files to github

- return created object

CLASS VARIATIIONSMODELVIEWSET

- Derived from ModelViewSet
- Uses VariationsSerialiser class
- Store all objects in queryset variable

METHODS

1. update\_vcs

This function is used for writing variations in json file

- Check id vcs\_path
- Call vcs\_push\_vari and pass the file path for variations.json
- vcs\_push\_vari
  - Get Variations object defined for that project from database
  - Check if intent name attribute is there is there in json file, if not add empty dictionary under that
  - Similarly do for language attribute , if not there add empty list under that intent and language
  - If text attribute not there in the json file under intent and language
  - Append it in the list created above
  - Write it down in the variations.json file

1. Create

This function is used for creating variations and updating it to the yaml file

- Call Djangos create function for the request and store the object

- Get the project oblect having tenant=the tenant value we get from the COOKIES data in the request
- Create VCSutils object , VCSUtils constructor is called which basically clones the repository for defined project and token
- Call generate\_variations\_yaml already explained above
- return the object

1. get\_variations

This function is used for returning variations for defined intent and language

- Get intent object for given pk and store it in intent
- Get Entities object for given intent.project and store in entities
- Create a new dictionary called syn2ent
- Loop through the entities and add key entity.name
- Get all synonyms for the defined entity and loop through it
- Add the key synonym to the new dictionary and set the value as entity name
- Get all the variations wrt defined intent and language and for each language append the metadata(text,variation.id,entity,synonym) in a dictionary
- And return this dictionary

# BASE.PY

_viewsets/base.py_

_Contains Functions and classes which will be used by others_

## CLASS BBMODELVIEWSET

This class overrides the basic functions defined in ModelViewSet

METHODS

1. get\_queryset

- Returns search\_queryse

1. search\_queryset

- Call filter\_by\_project function
- filter\_by\_project (defined bellow)
- Get the search key and search value from request and with the help of that filter the objects obtained from above function

1. filter\_by\_project

- Using Djangos queryset functionality filter the objects based on project, which can be got by tenant value stored in the cookie

1. initialize\_request

- If tenenat is not available in the request.COOKIES then raise APIException, tenant cookie is not set
- Get the tenant name from the Cookie for the defined project
- Get Bb-User-Id from request header , if it is not there raise APIException
- If there is conent in request.body then load the body from json format to dictionary
- Set the key project with current project id
- Convert dictionary to json
- Call django's initialize\_request

1. perform\_vcs

- if vcs is None, create object by calling VCSUtils and passing Bb-Access-Token to it and store the above object in vcs

- VCSUtils constructor is called which basically clones the repository
- Call update\_vcs
- update\_vcs
  - this function is defined under every feature but it basicallythe json file is updated with various attributes
- Changes are then pushed to Github

1. Create

- Call djangos create functionality and save object in res
- Getting first object from all the objects filtered based on "id" and storing in current\_object
- Call perform\_vcs
- Returning res

1. Update

- If data in request doesn't match data in current object i.e. trying to change object name

Raise APIException

- Call Django's update functionality
- Call perform\_vcs
- Returning res

1. partial\_update

- Call Django's update functionality
- Call perform\_vcs
- Returning res

# ENTITY.PY

_viewsets/entity.py_

ENTITIESMODELVIEWSET

- Uses EntitySerializer class

1. Get all entities objects from database an store in queryset variable

METHODS

1. update\_vcs

This function is used for writing entity and its synonyms in ent2syn json file

- Check if ent2syn file exists
- If it does get all Entities object from database from defined project and entity name
- For a particular language in the defined entity , get all the synonyms and store the synonym list as a value for the key,language in a new dictionary
- Do above for all languages and store th dictionary in form of json in the ent2syn.json file

1. List

This function is used for returning a list of synonyms and its entity based on the request as response.

- Get all project objects for defined tenant(get it from COOKIES present in request) and store it in project
- Get all entities objects for defined project from database and store it in queryset\_original variable
- Call search\_in\_queryset(already explained above) , passing request and queryset\_original
- Store the return(List of objects) of above function in ent\_queryset
- Loop through the list and store certain attributes(id,name,project\_id,synonym) from object in res dictionary
- Store all synonym queryset for each entity in syn\_queryset variable
- Loop through the queryset and if language attribute value is not definef in the project languages sett then raise validation error(language doesn't exist)
- Create a dictonary having synonym list and synonym id and append it under language key defined under synonym key in res dictionary
- Store in queryset list and paginate the queryset for better looking response

SYNONYMMODELVIEWSET

- Uses EntitySerializer class

1. Get all entities objects from database an store in queryset variable

METHODS

1. update\_vcs

This function is used for writing sysnonyms and its entity in ent2syn json file

- Get all project objects for defined tenant(get it from COOKIES present in request) and store it in project
- Check if ent2syn file exists
- If it does read file an store dictionary in file\_data
- Get all synonym objects from database for defined project and entity and store in entity variable
- Loop through file\_data as key value pair
- If entity name attribute defined in object is equal to key in file passes,
  - then if language attribute doesn't exist in languages defined in project then raise validation error
  - if language attribute is not in the value of the key in the file then create an empty list as value for the key , language
  - append synonym in that list
- Write back to file ent2syn in json format

1. Create

This function is used for creating synonym in the database

- Get all project objects for defined tenant(get it from COOKIES present in request) and store it in project
- create object by calling VCSUtils and passing Bb-Access-Token to it and store the above object in vcs
- VCSUtils constructor is called which basically clones the repository
- If synonym in request already there in syn2ent\_dict for that vcs\_path then raise validation error ,synonym already exists
- syn2ent\_dict is a dictionary having key as synonym an value as entity
  - This is done for easy access in future
- Call django's create functionality and store object in obj
- Call generate\_syn\_yaml
- generate\_syn\_yaml
  - Create dataset directoty
  - Dump ent2syn.json
  - Preprocessor will help generate corpus file from synonyms.yaml
  - Push to github
- return obj

1. destroy

This function is used for deleting a synonym

- Get all project objects for defined tenant(get it from COOKIES present in request) and store it in project
- create object by calling VCSUtils and passing Bb-Access-Token to it and store the above object in vcs
- VCSUtils constructor is called which basically clones the repository
- Store language and synonym to delete from get\_object and store
- DELETE MIGHT CHANGE
- Push to github
- In the end delete is updated in database ,github and corpus
- Call django's destroy functionality.

# UTTERS.PY

_viewsets/utters.py_

UttersModelViewSets

Derives from BBModelViewSet

Creates UttersSerializer object

Get all utter objects from database

METHODS

1. update\_vcs

This function is used for writing down utters in the json .

- Create utters directory
- Get the current utter object
- Get necessary attribute values from current object like-\>name,language,channel\_code
- Call write\_individual\_utter by passing the above details
- write\_individual\_utter
  - If utter\_name.json doesn't exist then, create the file
  - Then convert model to dictionary and write in json format to the file

1. Create

This function is used for creating utters in the database for every language and channel

  - Get project object wrt to tenant key got from request.COOKIES
  - Loop through all languages defined in the project
  - Loop through all channels defined in the project for each channel
  - The above is done as we need to create utter for every language and channel defined in the project
  - Store the language in request.data["lang"] field
  - Store the channel in request.data["lchannel\_code"] field
  - Create an object in the database by calling create defined in super class
  - Cretate a dictionary which has objects data and it will be returned as response
  - Response structure example-\>

{ "english": { "whatsapp": { name: "" ,title: "" }, "twitter" : { name: "", title: "" }},{ "hindi": { "whatsapp": { name: "" ,title: "" }, "twitter" : { name: "", title: "" }}}

  - After loop finishes return the response
-

1. get\_summary

This function is used for returning a list of utters based on the request as response.

  - Get all utter objects from database that match project with tenant key got from request.COOKIES and store objects in queryset
  - Call search\_in\_queryset(explained above) ,passing queryset and request
  - If utter\_name matches the utter\_name got from request then , return Response(get\_utter\_summary)
  - get\_utter\_summary
    - This function will return a custom dictionary of utter in a particular format for display to frontend.
  - If above ( If ) fails then we need to return summary for every utter object in the queryset , store it in a list
  - Paginating the above response list for a better frontend view

1. update\_common

This function is used for updating the utter in the database,json and pushing update to github

  - Get project object wrt to tenant key got from request.COOKIES
  - Get utter\_name from the request
  - create object by calling VCSUtils and passing Bb-Access-Token to it and store the above object in vcs
  - VCSUtils constructor is called which basically clones the repository
  - Get all utter objects wrt project,utter\_name and seprate(value should be false)
  - Meaning of separate
    - Basically in frontend when user chooses to have different utter response for different channel then separate is True which results in calling another function
  - Call search\_in\_queryset(explained above) ,passing queryset and request and store the returned utter objects in a variable
  - If language is present in response then filter the above response by language
  - If channel\_code is present in response then filter the above response by channel\_code
  - Call Djangos update on the above filtered objects and pass the utter data got from response
  - Store utter\_name,utter\_data in a new dictionary. This will be sent as response
  - Go through all the utter objects present after filtering and call write\_individual\_utter(basically updates/creates json and fills utter data,explained above) for each of them
  - Push all changes to github by calling push\_files and return the response

# APICONF.PY

_viewsets/APIConf.py_

APIConfViewSet

Derives from BBModelViewSet

Creates APIConfSerializer object

Get all APIConf objects from database

METHODS

1. update\_vcs

This function is used for writing api data to json file

- Create apis directory
- Get the current api object
- Open the file and write all the current object information like id,name,data,proj\_id

#

#

# METADATA.PY

_viewsets/metadata.py_

No METHODS defined in classes here as we don't need to customize them.

ServiceTypeModelViewSet

Derives from BBModelViewSet

Creates ServiceTypeSerializer object

Get all ServiceType objects from database

ProductCategoryModelViewSet

Derives from BBModelViewSet

Creates ProductCategoriesSerializer object

Get all ProductCategories objects from database

AttributesModelViewSet

Derives from BBModelViewSet

Creates ProductCategoriesSerializer object

Get all Attributes objects from database

# PROJECT.PY

_viewsets/projects.py_

METHODS:

1. create\_project

- creates a github repository for the project

- If the repo is a copy\_repo, use copy\_repo\_func (from utils\_base)
- copy\_repo\_func
  - clones the repository to be copied and transfers
  - moves data to the new repository which got created in project serializer and pushes the changes to GitHub and
  - then removes local path
- Else if its an existing project, pull the remote repository to local repository by calling repo\_clone
- repo\_clone
  - removes the local path in case it exists and then clones the repository.
- If its an existing project or copying from a repo then import all the metadata of the project using EnsembleImporter.run\_import
- _EnsembleImporter_ class basically helps to bring all the data from github to database(Django)
- Call project\_metadata
- project\_metadata
  - works only for blank repositories.
  - Creates project metadata file and channel conf in master branch with default channel and language and pushes the changes

1. sync\_project\_to\_bot

- Used to sync project to bot by creating url to chat with bot for defined tennant and passing the data, a dictionary having repository url and repository branch

1. add\_channel\_lang

- Get all utters for defined project
- Update/create each utter by calling update\_or\_create\_utters (defined above)
- For each channel and for each language for every channel write utter to json file by calling vcs\_push\_utters (defined above)

PROJECTMODELVIEWSET

Derived from ModelViewSet

METHODS

1. create

  - Uses ProjectSerialiser class
  - It deserializes the JSON received and checks if its valid
  - Creates a github repository of project using create\_project
  - Sends back a HTTP 201 response. A cookie is set with a tenant unique key

1. get\_queryset

- Takes in the parameters and filters the query set to help search for the words.

1. Update

- Parses the arguments to get new lang, new channel and delete lang, delete channel
  - delete\_channel\_lang
    - If anything to be deleted channel or lang
    - Delete it in the utters in local repository
  - add\_channel\_lang
    - If anything to be added channel or lang
    - call update\_or\_create\_utters to create them
- Patch all the changes by calling patch\_proj\_metadata
- patch\_proj\_metadata
  - Updates new channel and language to project\_metadata
- Push them to git repo

1. update\_main

- Gets all the current project, channel, lang
- updates base class with proj,channel,lang in the args by calling Djangos update
- returns the tuple of added and deleted channel, lang

1. destroy

- Calls base class destroy i.e. deletes the project
- Project can't be deleted unless all related objects are deleted

CLASS ENSEMBLEIMPORTER

Object is created when you are using an existing project or copying from a repository

**Constructor variables**

- Project-\>Storing an project object having a particular tenant value
- vcs\_path-\>Local repository path
- Token-\>Storing token value wrt github repository
- vcs-\>Is an oject of VCSutils , VCSUtils constructor is called which basically clones the repository

METHODS

_run\_import_

Entry point function which calls all the other methods defined in Ensembl importer class

All the methods mentioned below defined in this class basically help in creating/updating the metadata in the database

1. _get\_customer\_journey_

  - Looping through all the customer journeys cloned in the local computer
  - Extracting the customer journey name by replacing .json by "" from file name
  - Validation Error raised if customer journey name empty
  - Check if customer journey already exists in database
  - If it doesn't create a customer journey in the database by filling cj\_name,cj\_data and project attributes

1. _get\_intents_

  - Check if intent\_metadata.json in vcs path
  - Opening the file and looping through each key value pair
  - If key (intent name) is empty raise validation error
  - If the value(which is a dictionary) has a key called project\_id, the key value pair is deleted
    - It is deleted as this key is not longer needed
  - If the value(which is a dictionary) has a key called id, the key value pair is deleted
  - If intent has a customer journey reference
    - Store the customer journey object having that customer journey reference value in a new dictionary created called defaults having the key cj\_refernce
  - If intent is there then update it with the new values otherwise create a new intent in the database
    - The above is done by update\_or\_create functionality of Django

  - Since entities and utters are dependent/linked to an intent they are also updated/created by calling the function-
    - _update\_or\_create\_entities_
      - **Parameters**
        - project-\>project object for identification
        - split\_camel\_case(x) -\>List of intent which is split by camelCase i.e. List of entities

        - token-\> token value wrt github repository

        - vcs\_push-\>Bool value
      - In this function
        - Looping through the list
        - Creating the entity in databse
        - If vcs\_push is set to True then vcs\_push\_ent function is called
        - vcs\_push\_ent
          - if vcs is passed to this function get the vcs with help of project and token passed and set push variable to true
          - if utter\_name(list of split intent) is passed then store list(utter\_name) in entity

else create the list with the help of Intents.objects.filter(project=project)

          - Loop through entity and write the individual entities with the help ofwrite\_individual\_entities function
            - write\_individual\_entities
              - Loop through the utter\_name
              - Get entity wrt to split intent words with help of Entities.objects.get
              - Check if ent2syn.json present in directory, if not create a dictionary called file having a key as entities name
              - Value to that key is also a dictionary having keys of all languages selected in that project
              - The key lang has a value which is a list of synonyms which is fetched for that language with the help of ent.synonym\_set.filter(lang=lang)
              - Finally write the file dictionary as json to ent2syn.json
          - If push variable is true then call push\_files
            - push\_files
              - Push changes to GitHub

        - update\_or\_create\_utters
          - Here default utter data is created
          - Loop through every chanel set in this project
          - For each chanel loop through languages set in this project
          - If utter doesn't exists for given project,chanel,language and utter\_nmae then create the utter in the database
          - If vcs\_push is set to True then call vcs\_push\_utters
          - vcs\_push\_utters
          - if vcs is passed to this function get the vcs with help of project and token passed and set push variable to true
          - if utter\_name(list of split intent) is passed then store list of a dictionary having key and value pair as utter\_name else create a list containg a dictionary with key "utter\_name" and value as list of utter\_name belonging to that project,lang and channel defined

          - Loop utter and write the individual utter with the help of write\_individual\_utter function
            - write\_individual\_utter
              - Check if uttername.json file present for the language and channel defined
              - If it doesn't then create a directory under the defined language and channel and write uttername.json by getting the utters model for that language and channel in the form of a dictionary
          - If push variable is true then call push\_files
            - push\_files
              - Push changes to GitHub

1. _get\_utters_

- For every channel in defined project
- For every language in defined channel and project create a directory
- Loop through all json files in directory and store json data to tmp variable
- Deleting all unnecessary attributes defined and setting new attributes
- If data in sting format, format it to json and store in tmp['utter\_data']
- If utter exists in database update it else create it

1. _get\_entities_

- if ent2syn.json prent in vcspath open the file and read entity name and synonym list as key value pair
- if entity name empty raise validation error
- create entity in the database

1. _get\_context\_metadata_

- If intent\_metadata.json exists open the file and read key value pair

- If service\_type , product\_category , property exist create them in database respectivley

1. get\_variations

- Check if variations.json in vcs path
- Parse the json file as key,value pair where key is intent\_name and value is a dictionary
- Parse the value dictionary
- If the key is in the language defined in the project ,Loop through the value text and create variation for it respectively in database

1. get\_function\_flow\_mapping

- Check if function directory exists in vcs path
- Extract function name by replacing .json with ""
- If function name is empty raise validation error
- Loop through all the functions and create them in database if they don't exist

1. _get\_apis_

- Check if apis directory exists in vcs path
- Go through the files in directory and check if file name is ".json", if it is then raise validation error
- Read the json file
- Deleting attribute/keys not required in the json file like ["id"]
- Setting the metadata in the file
  - Project\_id,api\_name and api\_data
- Check if api exists in database, iif it doesn't create it in the database

1. _get\_synonyms_

- Check if ent2syn.json file exists in vcs path
- Open the file and parse it as key value pair, key is entity name and value is a dictionary
- Parse the value dictionary as key value pair, where key is language and value is synonym list
- If language is in language defined in the project ,parse through the synonym list and create the synonym for the defined project ,language and entity linked to it in the database

1. get\_code\_sandbox

- Parse through the sandbox files present in the sandbox directory ion the vcs path
- Extract sandbox name from file name by replacing ".json" with ""
- Call sandbox\_data\_from\_name ana store the returned data in sandbox\_data
- sandbox\_data\_from\_name
  - Extract the sandbox\_id (list) from the file name by splitting the name on "-" and replace the last list item ".py" with empty string
  - Parse through the customer journeys present in the customer journey directory present in the vcs path
  - Read the json files and store the dictionary in cj\_data variable
  - Call fuction sandbox\_data\_from\_flow\_json, pass cj\_data,sandbox\_id,sandbox\_name and sandbox\_data=None
  - Store the return value in sandbox\_data
  - sandbox\_data\_from\_flow\_json
    - If sandbox data present in "nodes" attribute in cj\_data for the defined sandbox\_id in the database , return the sandbox data
    - Else parse throught the values of cj\_data["Nodes"]
    - If the the value for the key "type" is "code\_sandbox" and for the the key "metadata" ,(the value is a dictionary) and the key in the value diactionary , "name" is the sandbox name then return that value of cj\_data["Nodes"]
  - If sandbox\_data is not None then go through the files in functions directory
  - Read each file and collect sandbox'\_data by calling sandbox\_data\_from\_flow\_json
  - Return the sandbox\_data
- After getting the sandbox\_data check if Sandbox for the given name,language ,project and code exists in database
- If it doesn't create the sandbox in the database

# FILE HANDLERS

_File Handlers_-For handling import/export operation executed by user

SCOPE.PY

This is for project

_file\_handlers/scope.py_

METHODS

- decode\_utf8
  - Gets excel file as input
  - Reading through the file and decoding it to utf-8-sig format
  - Returning generator object
- is\_camel\_case
  - Gets string as input
  - Returning bool value
  - True if string is not in uppercase, string doesn't have ('\_' and ' ' ) and string is alphanumeric
  - Else returning False
- update\_or\_create\_utters

This function is used for creating utters ,updating json and pushing to github

  - Gets project,utter\_name,token,vcs\_push and vcs as input
  - Default utter response is created in the form of a dictionary and stored in utter\_data variable
  - Loop through every channel present in defined project
  - Loop through every language present in defined project for every channel
  - Check if utter object is present for defined project,channel\_code,language and utter\_name
  - If not create the utter object by passing above information
  - If vcs\_push is set to True, callvcs\_push\_utters which is defined bellow
- vcs\_push\_utters

This function is used for writing utter from database to json file and pushing it to github repo

Gets project, channel\_code,language utter\_name,token and vcs as input

  - If vcs=None,

Create VCSUtils object, VCSUtils constructor is called which basically clones the repository and set push as True

  - If utter\_name is not None,

Create a list (utters) having a dictionary containing the utter\_name

  - Else Get utter names by getting the utter object having the defined project,language and channel\_code and append a dictionary for each utter\_name to the list called utters
  - Loop through the list and call write\_individual\_utter which is defined bellow
  - If push is set to True call push\_files, pushes changes to github

- write\_individual\_utter

This function is used for writing utter from database to json file

  - Gets channel\_code,language utter\_name,token and vcs as input
  - Check if uttername.json file present for the language and channel defined
  - If it doesn't then create a directory under the defined language and channel and write uttername.json by getting the utters model for that language and channel in the form of a dictionary
  - Write to the json file by converting model to dictionary for all the utter objects having the defined project,language and channel\_code
- update\_or\_create\_intents

This function is used for updating/creating intents in the database

  - Gets project,row,cj\_refernce as input
  - Update\_or\_create intent objects for defined project, intent name in defined excel row and defaults(which are got from excel row and are present as key value pair in defaults dictionary)
- vcs\_push\_intents

This function is used for writing intents to intent\_metadata.json

  - Gets project,vcs as input
  - Read intent\_metadata.json file
  - Loop through all intent objects for defined project
  - Convert the object to dictionary and write it under intent name in the json file
  - If cj\_refernce present for that intent object then write it down in json file under cj\_refernce for defined intent name
- update\_or\_create\_entities

This function is used for creating entities in the database and updating it to json and pushing to github

  - Gets project, utter\_name,token,vcs\_push and vcs as input
  - Loop through the utter\_name list which consists of the entities
  - Create entity object for defined project and entity name
  - If vcs\_push is True, callvcs\_push\_ent which is defined below
- vcs\_push\_ent

This function is used for writing entities and its synonyms in ent2syn json file and pushing changes to github repo

  - Gets project,token,utter\_name an vsc as input
  - If vcs=None,

Create VCSUtils object, VCSUtils constructor is called which basically clones the repository and set push as True

  - If utter\_name is not None, entity is list of utter name
  - Else callsplit\_camel\_case(defined above)for each intent object with the defined project
- Loop through all the entities and callwrite\_individual\_entities(defined bellow )for each entity
- write\_individual\_entities

This function is used for writing down entities and its synonyms in ent2syn json file

  - Get utter\_name ,vcs
  - Loop through the utter\_name list which consists of the entities
  - Get the entity object for the defined entity name and project
  - Checking if able to json read ent2syn.json file
  - If able ,store dictionary in variable called file else create it
  - Get all synonyms for defined entitity object(foreign key) and language and store it under language key
  - Do above for all languages defined in the project fo that entity
  - Store the language under entity name key
  - Write file dictionary to the ent2syn file
- update\_or\_create\_cj

This function is used creating the customer journey in the database and updating/creating the customer journey and utter mapping in the database

  - Get project, token, utter\_name, vcs\_push and vcs as input
  - Store random uuid using uuid4 and store it in start\_node\_id and utter\_node\_id
  - Fill the cj\_data based on the input
  - Try to fetch the customer journey object from database for defined project and cj\_name
  - If it fails then create the customer journey object
  - Update or create UtterCJMapping object for defined utter\_name,cj\_reference
  - If vcs\_push is True call vcs\_push\_cj(defined bellow)
  - Return cj\_refernce and cj\_data as a tuple
- vcs\_push\_cj

This function is used writing the customer journey data to the json file and pushing it to github repo

Gets project,token,utter\_name,cj\_data and vsc as input

  - If vcs=None,

Create VCSUtils object, VCSUtils constructor is called which basically clones the repository and set push as True

  - Check if both utter\_name and cj\_data is True, if it is then create a list called cj which contains a dictionary having utter\_name and cj\_data
  - Else get the data from cj objects for defined project ,store the data (utter\_name,cj\_data) in a dictionary and append it to a list called cj
  - Loop through cj and call write\_individual\_cj,(defined bellow)for each dictionary
  - If push is True, Call push\_files,pushes changes to github
- write\_individual\_cj

This function is used for writing the customer journey data to the json file

  - If cj\_utter\_name.json under cj doesn't exist then create directory
  - Write cj\_data to cj\_utter\_name.json
- import\_intent\_scope

This function is used for importing the intents i.e. Get the intents from the request and it is written to the database

  - Get all project objects for defined tennant , got from request.COOKIES
  - Get the excel file passed from the request
  - Call decode\_utf8(explained above), passing the excel file and convert each row to a dictionary using CSV.DictReader , store it in reader variable
  - Get "Bb-Access-Token" from request
  - Loop through the reader dictionary as key value pair ,where value is a dictionary containing the column name as key and its value as value
  - If the intent name(it's a column attribute) is not camel case raise APIException
  - Create a list having the intent name split to words using split\_camel\_case, and make the word lowercase
  - don't include the word context
  - convert the list to a string and join it by " "
  - Call update\_or\_create\_utters(defined above)
  - Call update\_or\_create\_cj(defined above)
  - Call update\_or\_create\_intents(defined above)
  - Call update\_or\_create\_entities(defined above)
  - The above is done basically for the imported csv file
  - The reason cj,utters and entities are created as they are created/updated whenever there is a change in intents, They are linked to intents
  - Create VCSUtils object, VCSUtils constructor is called which basically clones the repository and set push as True
  - Callvcs\_push\_utters (defined above)
  - Callvcs\_push\_ent(defined above) for all languages and all channels for every language
  - Call vcs\_push\_cj(defined above)
  - Call push\_files(defined above)
  - Return Response containing the message success
- export\_intent\_scope

This function is used for exporting the intents i.e. Get the intents from the database and it is written to the csv file and pushed to the cloud

  - Get all project objects for defined tennant , got from request.COOKIES
  - Create filename from the defined tennant for from request.COOKIES and datetime of system in a particular format
  - Open the new file as a csv
  - Writeheader() in csv file with the mentioned fields "Name", "ServiceType","ProductCategory","Product","Property"
  - Go through all intents objects got from database for defined project and create a dictionary data mapping the field with the value
  - Writerow() with the above data
  - Above is done for every intent
  - Call minio\_push, passing the filename,
  - MinIO is an object storage service compatible with S3 API.
  - Return Response containing the message success

VARIIATIONS.PY

_file\_handlers/variations.py_

METHODS

- check\_if\_intent\_exists

This function is used to check whether intent exists in the database or not

  - Gets intent name and project as input
  - Try to get intent object for defined project and intent name
  - If fails Raise APIException
  - Return the object
- replace\_bulk

This function replaces the entity with the respective synonym

  - Gets variation string and a dictionary having key as word in variation and value as its entity as input
  - Replace the key with the value in the variation string
  - Return string
  - It is basically replacing the words in the string with the entity
- preprocess\_text

This function is used for replacing the entities in the variation with its synonym

  - Gets syn2ent dictionary and variation as input
  - Replace '[',']' with ' ' from the variation
  - Loop through the words in the variation
  - If you are able to get entity for the synonym(the word) then add key value pair (word, entity for that word) to a new dictionary called replace\_map
  - Call replace\_bulk (defined above) and get the new string from it
  - Return new string
- generate\_system\_variations

This function is used for creating variations by using default variations defined and creating permutations with the entites

  - Gets \*\*kwargs as input
  - If language not there in \*\*kwargs , return empty list
  - Else Get language from kwargs and delete it from kwargs
  - Get all intent objects for the defined attributes in \*\*kwargs
  - Get all entities objects for defined project of the defined intent
  - Loop through all the entities object
    - Create sy2ent dictionary by adding key as entity name and value as entity name as (synonym of a word is also the word)
    - Loop through all synonyms objects for the defined entity(foreign key)
    - Then map the entity name to each synonym
  - Get the entities from the intent by calling split\_camel\_case(defined above)
  - If USE\_ENTITY\_PERMUTATIONS\_FOR\_DATASET is set to True in env file then get the permutations( all possibilities) for for the entites by calling permutations
  - Loop through all the permutations and for the defined language get the system generated permutations and combine it with the each permutation

Append all the above combinations to a list and return it

- generate\_variations\_yaml

This function is used for creating variations and updating it to the yaml file

  - Gets the path as input
  - Create datasets directory under path
  - Read variations.yaml and copy the json to Variations variable
  - Loop through the file in (dictionary format) as key value pair (intent,variation(called vari in code))
  - Loop through the value (variations ) which is a dictionary with key as language and value as variation(called var in code)
  - If Variations doesn't have intent as a key , create the key with its value being an empty dictionary
  - If language key not there in variations[intent], then create the key with its value being an empty list
  - Loop through var , remove each var from the list(variations[intent][lang])
  - Call preprocess\_text (defined above) , store the new sting in variable called text
  - Check if text is present in the list(variations[intent][lang])) , if not append it to the list
  - Dump variations to yaml file
  - Preprocessor will help generate corpus file from synonyms.yaml
  - Push files to github
- syn2ent\_dict

This function is used for creating a dictionary having key as synonym and value as entity

  - Read ent2syn.json file and store it as dictionary in ent2syn
  - Create a empty dictionary syn2ent to add synonym as key and entity as value mapping
  - Loop through the ent2syn dictionary as key value pair, key as entity and value as synonyms dictionary
  - Add key as entity and value as entity as entity is also a synonym of entity
  - Loop through synonyms dictionary and loop through the value(synonyms dictionary)
  - Add the synonym and entity mapping to syn2ent dictionary
  - Return the dictionary
- vcs\_push\_vari

This function is used for updating all changes to json file.

Gets project,vcs and f\_vari as input

  - Get all the variations objects for the defined project and loop through it
  - If the intent name for the variation is not there in f\_vari(variations json file), Then add the key value pair(intent name for defined variation ,{})
  - If the language for the variation is not there under the intent name for the variation in f\_vari(variations json file), Then add the key value pair(language key under the intent name for defined variation ,[])
  - If the text for the variation is not there in under the language key under the intent name for the variation in f\_vari(variations json file),Then append it to the list defined under the language key
  - Write all changes to variations.json

- create\_model\_variations

This function is used for creating variations in the database

  - Gets intent\_variations(dictionary having (language and its variation) mapping to the entity) and project as input
  - Loop through the intent\_variations as key value pair (intent and (language and its variations dictionary))
  - Loop through language and its variations dictionary where key is language and variations(list) is the value
  - Try to create the variation for the defined project and intent name,if it fails means variation already exist
- push\_vari\_yaml\_json

This function is used for updating all changes to json file and for updating all changes to yaml file.

  - If variations.json exists store all the data from variations.json in the form of a dictionary otherwise create an empty dictionary
  - Call vcs\_push\_vari to update changes in the json file
  - Call generate\_variations\_yaml to update changes in the yaml file
- import\_variations

This function is used for importing the variations i.e. Get the variations from the request and it is written to the database

  - Get all project objects from the database for the define tennant got from request.COOKIES
  - Get the language and get the file sent by user from the request
  - Create VCSUtils object for defined "Bb-Access-Token" got from request , VCSUtils constructor is called which basically clones the repository and set push as True
  - Read the excel file in the form of a list of a dictionary where each value in a list represent a row of the file (dictionary) and in that dictionary the key is the column name and value is the value of that column
  - Create a new dictionary intent\_variations, this is to basically get all the data from the excel file to this dictionary
  - Loop through that list if the value of the key intents is not there in intent\_variations, then create that key value pair (value of the key intents,{}) in intent\_variations
  - If the language got from request is not defined in the intent\_variations[value of the key intents] dictionary, then create that key value pair (language,[]) in intent\_variations[value of the key intents]
  - If the value of the key variations exists then append it to the list defined under the key language iwhich is defined under intent\_variations[value of the key intents]
  - Loop through all intent objects got from the database for defined project and it is got in the form \<QuerySet [intentname1, intentname 2]\> because of values\_list('name', flat=True)
  - Call generate\_system\_variations ,and add the returned list to intent\_variations[intent][lang]
  - Delete all old variation objects for defined project and language from Database
  - Call create\_model\_variations ,to create variations in the database
  - Call push\_vari\_yaml\_json, to push changes to yaml file and json file
  - Return success message as response
- export\_variation

This function is used for exporting the variations i.e. Get the variations from the database and it is written to the csv file and pushed to the cloud

  - Get all project objects for defined tennant , got from request.COOKIES
  - Get language from request
  - Create filename from the defined tennant for from request.COOKIES and datetime of system in a particular format
  - Open the file as csv
  - Define the fields to be written as header to the csv "Intents", "Variations"
  - Loop through all intent objects got from database for defined project
  - Get all variations for the defined intent(foreign key) and language
  - If not variations got then writerow in csv ("Intents": intent's name, "Variations": "")
  - If variations received, then loop through it and writerow in csv("Intents": intent's name ,"Variations": variation) for each variation
  - Call minio\_push, passing the filename,
  - MinIO is an object storage service compatible with S3 API.
  - Return Response containing the message success

UTTERS.PY

_file\_handlers/utters.py_

METHODS

- update\_utter

This function is for updating the utter data in the database

  - Gets prev\_row(prev row in database), project, request, temp\_qr(list of quick replies), temp\_btn(dictionary having key as card\_id and value is a list of button data) and utter\_name as input
  - Try to get the utter object from the database for defined utter name,
  - If it is there
    - then update the utter data in the database
    - Get buttons only if it has title and for card\_id having the same card\_id value of prev row and store it in a list called buttons
    - Create card dictionary with all prev\_row data and buttons just created by prev row data
    - Check if utter\_data["data"] has the card by checking if it leads to index out of range error , if doesn't just update it
    - Else append it
    - utter\_data structure looks like {'data':list of cards,'quick\_replies':List of quick replies,'quick\_replies\_slot':quick\_replies\_slot }
    - Update utter\_data["quick\_replies"] by getting the quick replies (only if they have a title) from temp\_qr
    - Update utter\_data["quick\_replies\_slot"] with help of prev\_row
    - Update the database by calling update pass the utter data and separate=True(as this utter is being updated for a particular language and channel)
  - Else , Raise Error(Utter doesn't exist)
- category\_append

This function is for getting button data/quickreply data depending on category passed to the function and storing it in a paaricular format

  - Gets row(current row in database), temp(can be dictionary to store button data or list to store quickreply) and category(can be button or quickreply) as input
  - Get the data(title,payload and type) from the row based on the category
  - If category is a button
    - Check if card\_id is in temp(a dictionary as category is button) , if it is then append the data under key row['card\_id'] else create an empty list under row['card\_id'] and append data to that list
  - Else(category is quickreplies)
    - Append data to temp(a list as category is quickreplies
- delete\_unwanted\_keys

This function is for deleting unwanted keys

- extract\_btn

This function is for updating data dictionary(dictionary with key(title,payload and type))

  - Gets btns(dictionary having key as card\_id and value is a list of button data),data( dictionary with key(title,payload and type)),i(index) and category(button/quickreply) as input
  - Try to get data from btns for defined index and store in data dictionary
  - If it fails as index error(means that the card doesn't have that category) then fill the data dictionary with ""
  - Return the data dictionary
- fill\_data

This function is for returning cards data(excluding buttons and quickreplies) for an utter

  - Gets utter as input
    - Get utter\_data from database , if it doesn't exist thgen get it from the json file and store in utters(structure looks like {'data':list of cards,'quick\_replies':List of quick replies,'quick\_replies\_slot':quick\_replies\_slot })
  - If 'data' key is not there in utters, set flag as True
  - Else
    - Loop through utters["data"] dictionary where value is a dictionary with card data
    - Get the all the necessary data from utter, data dictionary and update it in the utters["data"] dictionary value and also append the utters["data"] dictionary value in an empty list called(utter\_data)
    - Return the utter\_data,utters and flag
- call\_update

This function is for calling update operations for updating to database and this function is called whenever there is a new card for the same utter while going through the excel sheet

  - Gets prev\_row(prev row in database), row(current row in database),project, request, temp\_qr(list of quick replies), and temp\_btn(dictionary having key as card\_id and value is a list of button data) as input
  - Get utter name of prev\_row
  - Call update\_utter (defined above)
  - Make prev\_row as current row
  - And update temp\_btn dictonary (dictionary having key as card\_id and value is a list of button data) with current row data
  - Return prev row and temp\_btn
- import\_utters(not explained code wise)

This function is for importing utters from utter to database , done with all the functions defined above

Basically it is going through the excel file , getting the data and storing it in such a way that it is easy to update in database

Update in database is called whenever there is a different card for same utter and after every utter

Data is stored in such a way -\>

    - For every utter, have a dictionary with keys(data,quick\_replies,quick\_replies\_slots)
    - data key-\>Value is a list of dictionary, where the dictionary has various card attributes
    - quick\_replies key-\>Has a list of quick replies for utter
    - quick\_replies\_slot key-\>Has a list of quick replies slots for utter
  - Get all the project objects for defined tennant got from request.COOKIES
  - Get excel file,channel\_code,lang from request
  - Set is\_first\_row as True (This is done as we don't have a row to compare to )
  - Convert excel file to dictionary in such a way , list of dictionary each dictionary represents a row with key as column name and value as column value for that row
  - Loop through the rows
  - If first row is true(This will only execute once and then condition will never satisfy)
    - Call category\_append() for buttons and for quickreplies
    - Basically getting all button data for a row and quickreplies for a row
    - Set is\_first\_row to False as now we can compare to prev row
    - Comparing is done so that we can get all buttons and quickreplies for a card
  - Elif the previous row utter\_name is equal to current row utter\_name
    - Call category\_append() for buttons and for quickreplies
    - Basically getting all button data for a row and quickreplies for a row
    - If previous row card\_id is notequal to current row card\_id
      - Call call\_update as after every different card, update to database should be done
      - Call update\_utter
  - Else(previous row utter\_name is not equal to current row utter\_name)
    - Call call\_update as after every different utter, update to database should be done
    - And temp\_qr is filled with new values as current row has a new utter
  - After the for loop if temp\_btn,temp\_qr is empty then category append is done respectively
  - Is called again as the final utter was not accounted for and need to be updated in the database
- export\_utters(not explained code wise)
  - This function is for exporting utters from database to utter, done with all the functions defined above
  - Basically, the data is got from database
  - Data got is formatted properly
  - Then the formatted data is written to a csv file and exported
  - Get all the project objects for defined tennant got from request.COOKIES
  - Get excel file,channel\_code,lang from request
  - Create filename from the defined tennant for from request.COOKIES and datetime of system in a particular format
  - Define all the fields required in the excel
  - Writing in csv using DictWriter
  - Write all the fields
  - Get all utter objects from database for defined project,channel\_code and language
  - Call fill\_data (function is for returning cards data(rows(utter\_data),utters,flag) for an utter )
  - If flag is True, skip the utter
  - Get all buttons and quickreplies from the utters and store in buttons and quickreplies respectvley, if not present store []
  - Loop through utters["data"][1:] as a key value pair
    - if key buttons is present then store the button data under the card\_id as key in card\_btn dictionary
  - Loop through utter\_data as a dictionary
    - Call delete\_unwanted\_keys
    - Loop through dictionary as key,value pair
      - If key not in fields defined then delete it
    - If the card\_id is "card-0"(only there because only card\_0 has quick replies)
      - Get the max between button in card-0 and quick-replies
      - If max is equal to 0
        - Write that utterdata to csv(excludes button and quick replies, fills column with empty value)
      - Loop till max value
        - Call extract\_btn for both button and quickreplies to get its data ,(fills data with button and quickreplies data)
        - Write data to csv
    - If any other card\_id and button data is present for that card
      - Loop through the no of buttons (not checking max between button and quickreply as quick reply is not there in any card except 0)
        - Call extract\_btn for button to get its data ,(fills data with button data)
        - Write data to csv
    - If any other card\_id and button data is not present for that card

        - Write data to csv
  - Call minio\_push, passing the filename,
  - MinIO is an object storage service compatible with S3 API.
  - Return Response containing the message success

ENTITY\_SYN.PY

_file\_handlers/utters.py_

METHODS

- generate\_syn\_yaml

Function is for dumping to yaml

  - Create dataset directoty
  - Dump ent2syn.json
  - Preprocessor will help generate corpus file from synonyms.yaml
  - Push to github
- ent\_syn\_push

Function is for writing the synonyms for defined language and entity in json file

  - Read the "ent2syn.json" file,store dictionary in f\_entity
  - Loop through all entitiy objects for defined project got from the database
  - Get all synonyms for defined entity(foreign key) and language and store it as a list under key lang
  - Do the above for all languages and store this dictionary under key as entity name in the f\_entity dictionary
  - Write f \_entity to the json file
  - Call push\_files
- import\_entities

Function is for importing the entites from csv got from request to database(2 tables involved , entity and synonym)

  - Get all the project objects for defined tennant got from request.COOKIES
  - Get excel file,channel\_code,lang from request
  - Reading csv using DictReader and storing it iin dictionary format in reader
  - Create a new dictionary,data
  - Loop through the file as dictionary(reader) as row
    - A key,value pairs to the data dictionary
    - If key( Name) not in data then make the key value in data dictionary as (row['Name'] and [])
    - If synonyms present for defined entity , append the synonyms to the list defined under row['Name']
  - Create VCSUtils object for defined "Bb-Access-Token" got from request , VCSUtils constructor is called which basically clones the repository and set push as True
  - Loop through the data as key value pair
    - Try to Get the entity object from the database for defined project and key(which is the name)
    - Loop through the value
      - Try to get the synonym object for defined entity,language and synonym name
      - If not there create the synonym object in the database
    - If not able to get entity , Create the entity and for the defined entity create all the synonym by looping through the value
  - Call ent\_syn\_push for updating json
  - Call generate\_syn\_yaml for dumping to yaml file
- export\_entities

Function is for exporting entites from database(2 tables involved , entity and synonym) and writing to csv file

  - Get all the project objects for defined tennant got from request.COOKIES
  - Get excel file,lang from request
  - Create filename from the defined tennant for from request.COOKIES and datetime of system in a particular format
  - Define all the fields required in the excel
  - Writing in csv using DictWriter
  - Write all the fields
  - Loop through all Entity objects for defined project
  - Get all synonyms for defined entity(foreign key) and for defined project
  - If no synonyms retrieved then write in csv the entity name and leave synonyms column entity
  - Else loop through the synonyms and write entity name and a synonym in csv for each synonym
  - Call minio\_push, passing the filename,
  - MinIO is an object storage service compatible with S3 API.
  - Return Response containing the message success

#

#

#

#

#

#

#

#

#

#

#

#
# **SERIALIZERS**

_SERIALIZERS__-_Serializers in Django REST Framework are responsible for converting objects into data types understandable by javascript and front-end frameworks

# PROJECT.PY

_serializers/projects.py_

## CLASS PROJECTSERIALIZER

This class is derived from DRF.ModelSerializer and builds a JSON Response corresponding to all the fields entered by user in the form. It validates the constraints of various entries.

Class variables:

_Tenant_ - (required = False)

_repo\_url_ - (required = False)

_copy\_repo_ - (required = False)

(required=False)-\> means it allows the object attribute or dictionary key to be omitted from output when serializing the instance.

METHODS:

1. _ **get\_validation\_exclusions** _-\> Overridden to add repo\_url

2. _ **validate** _

- Name of project should be non-empty
- tenant name should be lower case and only alphabets
- If POST and if is blank or copy, then GitHubClient::create\_repository
- Access to field - loop through users apart from self, and send invite for github collaboration, authorization
- returns the attrs

BASIC WORKFLOW WHEN YOU CALL THE API FOR CREATION

1. Clone github
2. Validations
3. Make updates/create data on database
4. Make updates in files
5. Push data to github