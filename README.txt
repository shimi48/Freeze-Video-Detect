Instructions how to use the project

Install ffmpeg on your computer

Run tomcat 8 on your computer

jDK 1.8

Build the project using maven this will create a war file under target directory (work very good with intellij, just use maven action and package using the lifecycle)

Deploy the war on your tomcat (again, this can be done with maven easily through intellij with the tomcat7 plugin)

Update properties file (under the webapp directory in tomcat 'WEB-INF\classes' prefs.properties file, set the right ffmpeg path depending on your platform)

Use the following video files:
https://storage.googleapis.com/hiting_process_data/freeze_frame_input_a.mp4
https://storage.googleapis.com/hiting_process_data/freeze_frame_input_b.mp4
https://storage.googleapis.com/hiting_process_data/freeze_frame_input_c.mp4

Using Postman you can now play with the system (i will assume the webapp name is 'Freeze':

parse video files (add all the files you want just create new assetId):
-----------------------------------------------------------------------
POST http://localhost:8080/Freeze/parse
content-type - application/json
Body:
{
	"assetId" : "1",
	"fileURL" : "https://storage.googleapis.com/hiting_process_data/freeze_frame_input_a.mp4"
}

The video is proccessed asynchronously and you can check the status until completed

get video file status:
-----------------------------------------------------------------------
GET http://localhost:8080/Freeze/status/{assetId} (example: http://localhost:8080/Freeze/status/1)

Possible status: pending, completed, failed, not-found

get video files analysis:
-----------------------------------------------------------------------
GET http://localhost:8080/Freeze/analyze/[assetId1,assteId2]  (example: http://localhost:8080/Freeze/analyze/1,2,3)

Can select any combination from the ingested assets

Delete asset:
-----------------------------------------------------------------------
DELETE http://localhost:8080/Freeze/delete/{assetId}  (example: http://localhost:8080/Freeze/delete/1)

