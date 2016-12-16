## SAM Release Notes

### Change Log


#### v1.88.0
```YAML
date: December 01, 2016, 12:50
dependencies: none
```

 * \#125 Added filter query parameters to group deployments page.
 * \#124 Convert environment name to lower case before fetching the order


#### v1.86.0
```YAML
date: November 28, 2016, 08:04
dependencies: none
```

 * \#123 Changed frontend validation to allow descriptions with max 1500 characters
 * \#122 Upgrade material ui


#### v1.85.0
```YAML
date: November 25, 2016, 13:14
dependencies: none
```

 * \#120 Deployment dashboard for all applications belonging to a group
 * \#121 Added more servers in testdata


#### v1.84.0
```YAML
date: November 15, 2016, 11:48
dependencies: none
```

 * \#119 Allow expiry to be set when signing jwt using the dropwizard oauth command


#### v1.83.0
```YAML
date: October 26, 2016, 14:41
dependencies: none
```

 * \#118 Changed description validation to allow 1500 chars.


#### v1.82.0
```YAML
date: October 25, 2016, 18:08
dependencies: none
```

 * \#117 Parse long timestamp when sent as parameters to the oauth2 command.
 * \#115 Added cancel for all create new pages and refactored common components.


#### v1.80.0
```YAML
date: October 24, 2016, 10:02
dependencies: none
```

 * \#113 Frontend app deployments design
 * \#114 Changed from Basic to Bearer token authorization
 * \#112 Update editor with new attributes when changed after mounting component
 * \#110 OAuth authentication support for the services


#### v1.76.0
```YAML
date: October 10, 2016, 15:38
dependencies: none
```

 * \#108 Added json editor for displaying attributes


#### v1.75.0
```YAML
date: October 05, 2016, 16:40
dependencies: none
```

 * \#107 Open login window synchronously to prevent popup blockers.


#### v1.74.0
```YAML
date: October 05, 2016, 13:54
dependencies: none
```

 * \#106 Added css transitions for toggling meta

 
#### v1.72.0
```YAML
date: October 04, 2016, 15:04
dependencies: none
```

 * \#24 Enable gzip in dropwizard
 * \#23 Added a babel environment for production without HMR
 * \#22 Updated configuration for integration tests
 * \#21 Exclude incorrect version of apache-commons
 * \#104 Add oauth configuration to index.html file using a mustache template
 * \#105 Webpack hot module replacement for dev
 * \#103 OAuth sign in
 * \#102 Save metaOpen state in local storage
 * \#101 OAuth2 endpoint for verifying users against Github


#### v1.66.0
```YAML
date: September 15, 2016, 09:14
dependencies: none
```

 * \#96 New page for creating servers


#### v1.65.0
```YAML
date: September 14, 2016, 10:15
dependencies: none
```

 * \#95 New buttons for refreshing and deleting all asset types


#### v1.64.0
```YAML
date: September 08, 2016, 09:16
dependencies: none
```

 * \#93 Ability to assigning assets to groups and change the existing assignment
 * \#91 New page for creating assets


#### v1.63.0
```YAML
date: September 06, 2016, 16:34
dependencies: none
```

 * \#90 New optional query parameter mergeDepth for PATCH of all asset types


#### v1.61.0
```YAML
date: September 05, 2016, 09:06
dependencies: none
```

 * \#87 Change owner group for application
 * \#86 Moved page components to new pages folder


#### v1.60.0
```YAML
date: September 02, 2016, 09:31
dependencies: none
```

 * \#85 Frontend remove subgroup


#### v1.59.0
```YAML
date: September 01, 2016, 08:36
dependencies: none
```

 * \#80 Add subgroup to a group


#### v1.58.0
```YAML
date: August 30, 2016, 08:26
dependencies: none
```

 * \#84 Added guards preventing trying to show non-existing information
 * \#83 Use webpack dashboard
 * \#82 Get, add, remove subgroups from groups and remove deployment from server


#### v1.57.0
```YAML
date: August 29, 2016, 12:30
dependencies: none
```

 * \#81 Auto completion for group id when creating application
 * \#77 Icons while updating items (loading and failure)
 * \#79 New endpoint for fetching the ids of all groups
 * \#78 Create application


#### v1.56.0
```YAML
date: August 17, 2016, 11:26
dependencies: none
```

 * \#75 Added error dialog to notifier


#### v1.55.0
```YAML
date: August 10, 2016, 10:02
dependencies: none
```

 * \#59 Frontend add group
 * \#57 PUT operations now supports updates and has correct REST url style with id!
 * \#64 Fetch-sagas now always pass along authenticated as last argument to API calls
 * \#65 Programatically validate webpack config
 * \#63 Use authenticated user for API Calls, everyone is allowed to read.
 * \#62 Production configuration of Webpack for default build
 * \#61 Created a CRUD-reducer/selector creator
 * \#58 Use combineReducers selector composition
 * \#55 Moved fetch logic for server, group and info to saga
 * \#53 Saga abstraction
 * \#52 Simple login flow with hard coded user


#### v1.54.0
```YAML
date: July 25, 2016, 18:25
dependencies: none
```

 * \#50 Moved fetch logic for assets to saga
 * \#51 Require parens on arrow functions
 * \#47 All application-related side-effects handled with sagas
 * \#49 Bugfix for update notifications showing incorrectly
 * \#48 Non-expandable list items are now links.


#### v1.53.0
```YAML
date: July 22, 2016, 16:06
dependencies: none
```

 * \#45 Update support with notifications
 * \#42 Editable name and description for applications


#### v1.52.0
```YAML
date: July 18, 2016, 13:12
dependencies: none
```

 * \#41 Added support for wildcard search
 * \#39 Added search results for applications, assets and groups
 * \#40 Added deployments tab to application view


#### v1.51.0
```YAML
date: July 14, 2016, 10:29
dependencies: none
```

 * \#37 Added patch method to group and get environments to server
 * \#38 Get deployments resources on application
 * \#36 Added search functionality for applications, assets and groups


#### v1.50.0
```YAML
date: July 12, 2016, 09:17
dependencies: none
```

 * \#35 Group filtering on tags


#### v1.49.0
```YAML
date: July 11, 2016, 12:44
dependencies: none
```

 * \#31 Support for setting group for applications and assets
 * \#30 Helper file for running docker
 * \#29 Adds ability to skip transpilation of ES6 features available in Chrome

 
#### v1.47.0
```YAML
date: July 07, 2016, 12:18
dependencies: none
```

 * \#24 Fixed lint errors in webpack config


#### v1.46.0
```YAML
date: July 06, 2016, 16:41
dependencies: none
```

 * \#23 Factory for fetch-reducers


#### v1.45.0
```YAML
date: July 06, 2016, 09:58
dependencies: none
```

 * \#18 Added version at the bottom of the menu and a release-notes page
 * \#20 Lint fixes
 * \#19 Updated readme in dockerfile to expose both server and DB ports
 * \#17 Added detailed server page


#### v1.44.0
```YAML
date: July 01, 2016, 16:29
dependencies: none
```

 * \#19 Added search functionality


#### v1.43.0
```YAML
date: July 01, 2016, 16:14
dependencies: none
```

 * \#20 Bugfix for incorrect mapping of environment in search for servers


#### v1.42.0
```YAML
date: June 30, 2016, 22:10
dependencies: none
```

 * \#18 Added servers list page and changed page header


#### v1.41.0
```YAML
date: June 30, 2016, 18:12
dependencies: none
```

 * \#17 Proxy services to dropwizard when running dev server


#### v1.40.0
```YAML
date: June 29, 2016, 15:00
dependencies: none
```

 * \#16 Added empty asset search result


#### v1.38.0
```YAML
date: June 29, 2016, 13:37
dependencies: none
```

 * \#15 Fixed whitespace checkstyle warning
 * \#16 Improved REST interface


#### v1.36.0
```YAML
date: June 27, 2016, 13:32
dependencies: none
```

 * \#15 Asset improvements
 * \#14 Added checkstyle config and fixed formatting
 * \#12 Add createAsset to asset service

 
#### v1.34.0
```YAML
date: June 20, 2016, 07:36
dependencies: none
```

 * \#13 Added new sub resource deployment to server


#### v1.33.0
```YAML
date: June 20, 2016, 06:49
dependencies: none
```

 * \#10 Add a Dockerfile which installs java, run mongod 3.2 and run the CMDB server
 * \#11 Fixed dependency type from server to front
 * \#8 Minor improvements of testdata.


#### v1.32.0
```YAML
date: June 17, 2016, 15:48
dependencies: none
```

 * \#14 Now using real routes. Also design fixes


#### v1.31.0
```YAML
date: June 17, 2016, 10:29
dependencies: none
```

 * \#7 Server index.html default on all paths

 
#### v1.29.0
```YAML
date: June 14, 2016, 08:25
dependencies: none
```

 * \#6 Added simple smoke test
 * \#5 Clean-up of Maven POM files. Added npm registry configuration.
 * \#4 Fixed line endings
 * \#3 Added applications page
 * \#2 Added .gitattributes with auto crlf, normalized line endings
 * \#12 Added logo to menu
 * \#11 Readded swagger documentation
 * \#10 Added maven build for frontend project. Removed old frontend


#### v1.24.0
```YAML
date: June 10, 2016, 17:05
dependencies: none
```

 * \#9 Changed public path


#### v1.23.0
```YAML
date: June 10, 2016, 16:32
dependencies: none
```

 * \#7 Add front-end project


#### v1.22.0
```YAML
date: June 10, 2016, 14:05
dependencies: none
```

 * \#8 Added new resource info with version and release notes


#### v1.21.0
```YAML
date: June 10, 2016, 08:43
dependencies: none
```

 * \#6 Added deployment as a link between a server and its deployed applications
 * \#5 Read support for asset and patch for application


#### v1.19.0
```YAML
date: June 07, 2016, 11:11
dependencies: none
```

 * \#4 Moved integration tests to new folder integration-test in release package


#### v1.18.0
```YAML
date: June 03, 2016, 15:16
dependencies: none
```

 * \#2 New assets and search endpoints
 * \#1 Integration tests initial commit

#### v1.2.0
```YAML
date: Maj 26, 2016
dependencies: none
```
* Initial release.
