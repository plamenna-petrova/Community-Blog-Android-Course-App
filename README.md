# COMMUNITY BLOG ANDROID COURSE APP

## Idea and implementation

The project implements registration and login functionality via Firabase Authentication. The registration can be completed with or without a profile image. 
When the app is started, the login form is the first component to be visualised.
In case a user doesn't have a profile yet, they should click on the default image. 
Then a redirection to the registration form follows. After login or registration alternatively the user will gain access to the Home Activity screen.
Once registered/loged in a user can add new posts with title, description and image by triggering the popup with a special drawable pen icon. 
The Home Activity makes use of the Home Fragment,
where a RecyclerView of the posts is being instanciated. On the left side, there is a header navigation drawer with a custom menu. The user can navigate through the three menu items.
However, the last two show only static dummy text. The first Home segment is previously refered as to the major component. The rear button of the drawer serves as logout. If clicked,
the login form appears. An onClickListener is being activated for each post item from the RecyclerView - the information of the objects is stored also in the Firebase Realtime Database 
The event loads the PostDetailsActivity. This activity displays detailed information about the posts and authors. Below there is another RecyclerView section, this time for comments.
Every user can comment on the posts of other users as well as on their, there is no limitations on commenting. The comments are stored in the Realtime Database, too - they have
a reference to the key of the post they belong to. All images, integrated in the project are saved in Firebase Storage - as strings, pointing to the downloaded links or as nullables.
The app supports portrait and landscape screen orientation when supporting. An essential details is the appearance of notification, after a new post is being added and the RecyclerView
refreshed.

## How to configure the project

Log into the Firebase Console with your Google account and follow the steps of creating a new project.
Add the necessary files to the build.gradle files of your project instance.

## The project

Community Blog works with Android Studio as IDE, embeds Firebase Bom, Firebase Auth, Firebase Storage and Firebase Database dependencies.

## File Structure

1. java/com/example/communityblog
  - Activities - have different functions and can be switched to, using intents
     - HomeActivity
     - LoginActivity
     - PostDetailsActivity
     - RegisterActivity
  - Adapters - adapting a single item to fit to a list of objects
     - CommentAdapter 
     - PostAdapter
  - Fragments - separate the logic into smaller parts
     - HomeFragment
     - ProfileFragment
     - SettingsFragment
  - Models - the object classes for construction, getting values from the database and setting values to a view
     - Comment
     - Post
2. res
  - drawable - folder, which contains mostly vector xml files and font icons, beautifying the layout files
  - menu - for the navigation drawer
  - layout - all xml template files, in the view of which the values are injected from the database or after some processing. Some do includes buttons and progress bars, too.
  Hence both static and dynamic content is being submitted.
  - values - for color, strings, dimensions, styles and themes values, playing the role of secondary helpers, when building the entire layout of the app  
3. AndroidManifest.xml 
Here activities and permissions are registered and the rotation of the devices is updated




