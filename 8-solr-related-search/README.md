#Solr Related Search



##Setup

To see this Related Search feature in action, follow these steps.

1. Download solr-related-search.zip and unzip it.
2. Make sure you have stopped any running instances of Solr on your computer (this zip file comes with its own instance of Solr that we will use.
3. Cd into the directory and run setupRelatedSearch.sh
4. You should see Solr start up as well as the simple server my code uses.
5. When you see `Building 75% > :runFinalJar`, you can start trying out Related Search

##Demo
1. Open your browser and go to `http://localhost:7777/search?q=disc`. This will give you the response of a standard Solr search for "disc" in addition to a list of searches related to "disc". You'll notice that "computer dvd disc" is last on the list.
2. Open another browswer tab and go to `http://localhost:7777/item-detail?id=9&search-terms=disc computer dvd`. This will show you the details for the item with id 9 and it increase the score of the search query "disc computer dvd". The idea is that when you click on a search result, it'll show you the item details and remember that "disc computer dvd" was a successful query.
3. Navigate back to `http://localhost:7777/search?q=disc`. You'll see that "computer dvd disc" went up in the list because we increased its score with a successful query. You'll also notice that it treated "computer dvd disc" and "disc computer dvd" as the same query.  The order of the terms doesn't matter.
