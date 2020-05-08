import pandas as pd

# Compile stats: fetch.csv
fetch = pd.read_csv("fetch_latimes.csv", header=None)

attempted = fetch.shape[0]
success = fetch[fetch[fetch.columns[1]] < 300].shape[0]
failure = fetch[fetch[fetch.columns[1]] >= 300].shape[0]

dict_of_status = fetch.groupby(fetch.columns[1]).count().to_dict()
dict_of_status = dict_of_status[0]

# Compile stats: visit.csv
visit = pd.read_csv("visit_latimes.csv", header=None)

total_extracted = visit[visit.columns[2]].sum()

# Size is in bytes

#1KB = 1024B 
#1MB = 1024KB
less_1kb = visit[visit[visit.columns[1]] < 1024].shape[0] # < 1KB
less_10kb = visit[(visit[visit.columns[1]] >= 1024) & (visit[visit.columns[1]] < 10*1024)].shape[0] # 1KB ~ <10KB
less_100kb = visit[(visit[visit.columns[1]] >= 10*1024) & (visit[visit.columns[1]] < 100*1024)].shape[0] # 10KB ~ <100KB
less_1mb = visit[(visit[visit.columns[1]] >= 100*1024) & (visit[visit.columns[1]] < 1024*1024)].shape[0] # 100KB ~ <1MB
greater_1mb = visit[visit[visit.columns[1]] >= 1024*1024].shape[0] #>= 1MB

dict_of_ct = visit.groupby(visit.columns[3]).count().to_dict()
dict_of_ct = dict_of_ct[0] # Have to clip top 1/2 when outputting to file


# Compile stats: urls.csv
urls = pd.read_csv("urls_latimes.csv", header=None)

unique_extracted = urls.shape[0]
unique_within = urls[urls[urls.columns[1]] == "OK"].shape[0]
uinque_outside = urls[urls[urls.columns[1]] == "N_OK"].shape[0]

# Output to file
out = "Name: Pradeep Lam\n"
out += "USC ID: 1863725555\n"
out += "News site crawled: mercurynews.com\n"
out += "\n"
out += "Fetch Statistics\n"
out += "================\n"
out += "# fetches attempted: " + str(attempted) + "\n"
out += "# fetches succeeded: " + str(success) + " \n"
out += "# fetches failed or aborted: " + str(failure) + "\n"
out += "\n\n\n"
out += "Outgoing URLs:\n"
out += "==============\n"
out += "Total URLs extracted: " + str(total_extracted) + "\n"
out += "# unique URLs extracted: " + str(unique_extracted) + "\n"
out += "# unique URLs within News Site: " + str(unique_within) + "\n"
out += "# unique URLs outside News Site: " + str(uinque_outside) + "\n"
out += "\n"
out += "Status Codes:\n"
out += "=============\n"
for key in dict_of_status:
	out += str(key) + ": " + str(dict_of_status[key]) + "\n"
out += "\n"
out += "File Sizes:\n"
out += "===========\n"
out += "< 1KB: " + str(less_1kb) + "\n"
out += "1KB ~ <10KB: " + str(less_10kb) + "\n"
out += "10KB ~ <100KB: " + str(less_100kb) + "\n"
out += "100KB ~ <1MB: " + str(less_1mb) + "\n"
out += ">= 1MB: " + str(greater_1mb) + "\n"
out += "\n"
out += "Content Types:\n"
out += "==============\n"
for key in dict_of_ct:
	out += key.split(";")[0] + ": " + str(dict_of_ct[key]) + "\n"

out = out[:-1] # Cut the last new-line

with open('CrawlReport_latimes.txt', 'w') as fp:
	fp.write(out)