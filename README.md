Rent App Design Project - README Template
===

# Rent App

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
-  A social network in which users can rent things and also put things for rent.
   

### App Evaluation
- **Category:** Shopping/Social 
- **Mobile:** The mobile is the most comfortable tool in which users can carry out purchase and rental operations. The camera is used to share images of the rented things, and the map is used to know the location where each thing is.
- **Story:** This application will be a service of great value for people since most people have things they do not need, from which they can obtain an extra income by renting them through the application without having to sell them. And vice versa, people who need to rent something can do it through the app.
- **Market:** Anyone who needs to acquire anything for a certain time or anyone who wants to rent their things.
- **Habit:** Users can access the application several times a day, either to rent a thing, or to put something for rent.
- **Scope:** 

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* The user will be able to create an account within the application.
* The user will be able to log into the application.
* The user will be able to register the following information in their profile (this information will be public for the other users) 
    * Profile Picture.
    * A brief description about them.
    * The items that they have for rent. 
    * Reviews  
    * Where they are located. 
* The user will be able to see a list of the categories of the items.
* The user will be able to see a list of items (the list will show the name, picture, cost and brief description of every item). 
    * In the home timeline
    * Inside a category
* The user will be able to search items:
    * In the home timeline
    * Inside a category
* The user will be able to see the information of an item in the Details View. 
    * Item Name
    * Item Picture 
    * Item Description
    * Item Conditions 
    * Cost of the rent per day
    * Item Location 
* The user will be able to request to rent an item. 

**Required Must-have Stories**
* The user will be able to see the list of items he has for rent.
* The user will be able to put an object for rent, registering the following information (this information will be public for the other users). 
    * Item Name
    * Item Picture 
    * Item Description
    * Item Conditions 
    * Cost of the rent per day
    * Item Location 
* The user will be able to capture a photo or select a photo gallery for the object in rent.
* The user will be able to see a list of the requests that he has in the items that he has for rent

**Optional Nice-to-have Stories**

* The user will be able to see the location of all the items on a map.
* The user can create an account through a Google or Facebook account.
* The user will be able to send images and videos through chat.
* Top up money within the app.
* The user can change the theme of the app (Ex. Light Mode/Dark Mode).
* The user can change the text size.

### 2. Screen Archetypes

* Login Screen
   * User can login.
* Registration Screen
   * User can create a new account.
* Profile
   * User can view their profile.
   * User can view the profile of other user.
* Stream
   * User can view the list of the categories.
   * User can view different lists of items.
   * User can view the details of an item.
* Details 
   * User can view the list of categories.
   * User can view the list of items for rent. 
   * User can view the details of an item. 
* Search
   * User can search for items in rent.
* Creation
   * User can modify their profile.
   * User can publish a new item for rent. 
   * User can capture a photo or select from photo gallery.
   * Option to capture current location when publishing an item.
   * Sending network request to create new valid content item
   
### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Screen
* Search
* Profile
* Publish an item

**Flow Navigation** (Screen to Screen)

* Login Screen
   * Home
* Registration Screen
   * Home
* Home Screen
   * Timeline of items in rent. 
   * Publish an item
   * Help
* Search Screen
   * Item Details 
* User List
   * User Profile
* Own Profile
   * Edit profile.
   * Own rented items.
   * Foreign rented items.
   * Recharge money. 
* Own Rented Item
   * Recharge money. 



## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models
#### User

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user |
   | username      | String   | user's name |
   | description   | String   | user's description |
   | profilePicture| File     | user's profile picture |
   | location.     | Pointer to Location  | user's location |
   | likesCount    | Number   | number of likes that the user has |
   | createdAt     | DateTime | date when user is created (default field) |
   | updatedAt     | DateTime | date when user is last updated (default field) |
   
#### Item
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the item (default field) |
   | owner         | Pointer to User| item's owner   |
   | images        | Array<File>    | item's images  |
   | isRented      | Boolean        | is the item currently rented? |
   | tenant        | Pointer to User| item's currently tenant |
   | price         | int | item rental price per day
   | likesCount    | Number   | number of likes for the item |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

#### Location
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the location|
   | country       | String   | country |
   | city          | String   | city |
   | ZIP      | int      | ZIP code |
   | createdAt     | DateTime | date when location is created (default field) |
   | updatedAt     | DateTime | date when location is last updated (default field) |
   
#### Rents
   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | rentId        | String   | unique id for the rent |
   | owner         | Pointer to user   | item's owner|
   | tenant        | Pointer to user   | item's tenant |
   | startDate     | Date     | startDate of the rent |
   | endDate       | Date     | endDate of the rent |
   | daysCount     | Number   | number of days of the rent |
   | totalPrice.   | Number   | total rental price 
   | createdAt     | DateTime | date when location is created (default field) |
   | updatedAt     | DateTime | date when location is last updated (default field) |  ### Networking
   
### Networking

#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all posts of items regarding location
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile information

### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all item rental posts where user is author
         ```swift
         let query = PFQuery(className:"Item")
         query.whereKey("author", equalTo: currentUser)
         query.order(byDescending: "createdAt")
         query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
            if let error = error { 
               print(error.localizedDescription)
            } else if let posts = posts {
               print("Successfully retrieved \(posts.count) posts.")
           // TODO: Do something with posts...
            }
         }
         ```
      - (Create/POST) Create a new like on a post
      - (Delete) Delete existing like
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
#### [OPTIONAL:] Existing API Endpoints
##### An API Of Ice And Fire
- Base URL - [http://www.anapioficeandfire.com/api](http://www.anapioficeandfire.com/api)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /characters | get all characters
    `GET`    | /characters/?name=name | return specific character by name
    `GET`    | /houses   | get all houses
    `GET`    | /houses/?name=name | return specific house by name

##### Game of Thrones API
- Base URL - [https://api.got.show/api](https://api.got.show/api)

   HTTP Verb | Endpoint | Description
   ----------|----------|------------
    `GET`    | /cities | gets all cities
    `GET`    | /cities/byId/:id | gets specific city by :id
    `GET`    | /continents | gets all continents
    `GET`    | /continents/byId/:id | gets specific continent by :id
    `GET`    | /regions | gets all regions
    `GET`    | /regions/byId/:id | gets specific region by :id
    `GET`    | /characters/paths/:name | gets a character's path with a given name
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
