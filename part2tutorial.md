# Project 3 Part 2 Tutorial
## Implement Camera and Sensor from Project 2
The first thing I did for part 2 was copy most of the code from Project 2 into Project 3. This included copying over all of the functions in MainActivity, making a file_paths resource, adding the ImageView for the bitmap and the TextView for the temperature, and adding the appropriate permisions to the manifest. Then in MainActivity, I changed the old onButtonClick function to updateDatabase(), which I then added to onActivityResult so that the database is only updated once the picture and temperature are recorded.

## Modify the Database
Next, I modified the database to handle the new information I wanted to store. In the LogEntry class, I removed COLUMN_NAME_ENTRY, and added an entry for the image filepath and an entry for the temperature. Then, in the sql string, I again removed the line for COLUMN_NAME_ENTRY, and added lines for the two new fields. Then, in the LogEntryDbHelper, I changed the version number of the database.

Back in MainActivity, I modified updateDatabase() to reflect the changes I just made. I also removed the code to clear out the text from the text entry. Lastly, I removed the text entry from the UI, since it was no longer needed. 