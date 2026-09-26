# Data collector android application for collecting activity related data at Hajmola activity arrangement at schools for Sample product review.

## App Flow

1. Login
2. Admin -> Admin Privilege page routing to :
   1. Admin Dashboard -> fetch school list as per filters
   2. Main Menu 
   3. Register User -> As Admin or User
   4. Update Excel Sheet -> Clear Old Data on Excel Sheet , Fetch fresh school list, Update Excel sheet
   5. Open excel sheet

3. User -> Main Menu
4. Main Menu -> Home Page for User
   1. School Data Home ->
      1. School Details -> School general info and images, Principal Info, Contact Auth Info
      2. Activity Details -> Activity Images, Activity related data
      3. Close button -> redirects to Main Menu
   3. Synchronize Button -> initiate filled data upload to the server
   4. Sync status -> Shows uploading status
   5. My Uploads -> List All Schools filled by User
   6. Logout -> Clear session, redirect to login page
