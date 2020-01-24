Description
===========

Write a program that downloads a set of video files from a given set of urls, runs a filter on each one and exposes its output into a useful format for consumption by other APIs. You can use the ffmpeg CLI for this, ie no need to work against its libs.

Requirements:
-------------
1. The data needs to be inverted to show when video was valid and should be stored as a series of points. This is based off freeze_start and freeze_end values. The unit of these values are seconds. For example, the above would translate to [0, 5.30], [7.36, 16.78]

2. Determines the longest period of valid video within each stream.

3. Determines the percentage of all aggregated valid video periods over the entire duration of the stream.

4. Determines whether the entire videos set is synced freeze-frame wise, iff all videos have the same amount of valid periods, and each period's corresponding 'start' or 'end' cross all videos are no more than 500 ms apart.

5. You'll return the following JSON structure:

{
   "all_videos_freeze_frame_synced":true,
   "videos":[
      {
         "longest_valid_period":7.35,
         "valid_video_percentage":56.00,
         "valid_periods":[
            [
               0.00,
               3.50
            ],
            [
               6.65,
               14
            ],
            [
               19.71,
               20.14
            ]
         ]
      },
      {
         "longest_valid_period":7.33,
         "valid_video_percentage":55.10,
         "valid_periods":[
            [
               0.00,
               3.40
            ],
            [
               6.65,
               13.98
            ],
            [
               19.71,
               20.00
            ]
         ]
      }
   ]
} 

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Instructions how to use the project
===================================

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

