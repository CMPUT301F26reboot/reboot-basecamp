\# Automation 

Automation application handles automatic logic, like lottery winners drawing. 

How lottery draws work:  
- draw.yml in the main applications workflows is run on GitHub every hour, where it creates a virtual machine, and tells it to run Draw.kt in automation. 
- Draw.kt checks for lotteries that are past their end date and haven't been drawn yet. For each lottery that qualifies, it randomly selects the lotteryWinners amount (or less) from the waitlist, and assigns them to the selectedList. It then sets drawn to true, and creates a notification for the winners and for the losers  

##### Note: Since this summer, GitHub has been quite unreliable with scheduled cron tasks. Workaround is outlined below:  

From Claude Opus 5.5, 11/25/2026:  

Q. " I have verified that draw.yml is correct, it runs every hour on the 17th minute, Draw.kt works as seen when I click "Run workflow", and main is the Default branch. Do you have any ideas why this may not be working?"  

A.  

Since approximately August 26, 2026, developers across various repositories have observed their automated tasks either failing to trigger entirely or running with delays of several hours. GitHub's status page continued to list Actions as "operational," with no public incident posted. Moving cron jobs to off-peak minutes was suggested but did not fix the problem, according to the reports.  



The usual workaround is to use an external scheduler such as a Cloudflare Worker cron trigger, Google Cloud Scheduler, or cron-job.org to send a POST to the workflow's dispatches API endpoint. That does exactly what your Run workflow button does, which already works instantly, just on a timer. cron-job.org is free and doesn't need a credit card. Your workflow already has workflow\_dispatch, so draw.yml needs no changes.  

1. Create a GitHub token that can only start workflows  
  

On GitHub, go to your profile picture → Settings → Developer settings → Personal access tokens → Fine-grained tokens → Generate new token.  

Resource owner: whoever owns the repo, you or your course organization.  

Repository access: choose Only select repositories and pick reboot-basecamp.  

Permissions → Repository permissions → Actions: Read and write. Leave everything else alone.  

Expiration: the end of your semester.  

Copy the token, which starts with github\_pat\_. You won't be able to see it again.  



If the repo belongs to an organization that blocks fine-grained tokens, a classic token with the repo scope also works.  



2\. Test it from PowerShell  



Replace OWNER with the account or organization name in your repo's URL:  



powershell 
``` 

$token = "github\_pat\_..."  

Invoke-RestMethod -Method Post   

&#x20; -Uri "https://api.github.com/repos/OWNER/reboot-basecamp/actions/workflows/draw.yml/dispatches"    

&#x20; -Headers @{ Authorization = "Bearer $token"; Accept = "application/vnd.github+json"; "X-GitHub-Api-Version" = "2022-11-28" }  

&#x20; -Body '{"ref":"main"}'  
```  



If it works, it prints nothing, and a new workflow\_dispatch run appears in your Actions tab. A 404 error usually means the owner or repo name is wrong, or the token can't access that repo.  



3\. Schedule it on cron-job.org  

Make a free account at cron-job.org and click Create cronjob.  

URL: the same https://api.github.com/repos/OWNER/reboot-basecamp/actions/workflows/draw.yml/dispatches.  

Schedule: every hour at minute 17, or whatever you prefer.  

Under Advanced:  

Request method: POST  

Headers:  

Authorization: Bearer github\_pat\_...  

Accept: application/vnd.github+json  

X-GitHub-Api-Version: 2022-11-28  

Content-Type: application/json  

Request body: {"ref":"main"}  

Save it, then use Test run. You should get a 204 response and see a new run in the Actions tab.  



Alternative: if you'd rather not give a third-party site your token, Google Apps Script can do the same thing from your Google account. Create a script with this function, store the token under Project Settings → Script properties as GITHUB\_TOKEN, then go to Triggers → Add trigger and choose a time-driven hourly timer:  


```

js

function triggerDraw() {

&#x20; const token = PropertiesService.getScriptProperties().getProperty("GITHUB\_TOKEN");

&#x20; UrlFetchApp.fetch("https://api.github.com/repos/OWNER/reboot-basecamp/actions/workflows/draw.yml/dispatches", {

&#x20;   method: "post",

&#x20;   contentType: "application/json",

&#x20;   headers: { Authorization: "Bearer " + token, Accept: "application/vnd.github+json" },

&#x20;   payload: JSON.stringify({ ref: "main" }),

&#x20; });

}
```

