<?php

header('Content-Type: text/html; charset=utf-8');
error_reporting(0);
 
$limit=10;
$query=isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$sort=isset($_REQUEST['rank'])&&$_REQUEST['rank']=='pagerank' ? array('sort'=>'pageRankFile desc') : false; //存疑，看一下
$results=false;

if ($query) {
	 require_once('Apache/Solr/Service.php');
	 
	$solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/');

	 if (get_magic_quotes_gpc()==1){
	 	$query = stripslashes($query);
	 }

	$additionalParameters=array(
		'fq'=>'a filtering query',
		'facet'=>'true',
		'facet.field'=>array(
			'field_1',
			'field_2'
		)
	);

	 try {
	 	if($sort){
	     	$results=$solr->search($query,0,$limit,$sort);
		}else{
	 		$results=$solr->search($query,0,$limit);
	 	}
	 } catch (Exception $e) {
	 	die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
	 }
}
?>

<html>
	 <head>
	 	<title>PHP Solr Client Example</title>
	 </head>
     
	 <body>
		 <form accept-charset="utf-8" method="get">
			 <label for="q">Search:</label>
			 <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query,ENT_QUOTES,'utf-8'); ?>"/>
					 <input type="radio" name="rank" value="lucene" checked="checked" <?php if(isset($_REQUEST['rank']) && $_REQUEST['rank']=="lucene") echo "checked";?>>Lucene
                     
		      		 <input type="radio" name="rank" value="pagerank" <?php if (isset($_REQUEST['rank']) && $_REQUEST['rank']=="pagerank") echo "checked";?>>Page Rank
		      	 
				 	<input type="submit"/>
		 </form>

<?php
if ($results){
	$total=(int) $results->response->numFound;
	$start=min(1,$total);
	$end=min($limit,$total);
?>

<div>Results: <?php echo $start; ?>-<?php echo $end;?> of <?php echo $total; ?>:</div>

<ol>
<?php
 foreach($results->response->docs as $doc){
?>

<li>
	 <table style="border:1px solid black;text-alogn:left">
<?php
$dictInfo = array();
foreach ($doc as $field => $value) {
	if($field=="title"||$field=="og_url"||$field=="id"||$field=="og_description"){
		$dictInfo[$field] = $value;
	}
}
?>

	<tr>
		<th>Title:</th>
		<td><?php echo "<a href='".htmlspecialchars($dictInfo["og_url"],ENT_NOQUOTES,'utf-8')."'>".htmlspecialchars($dictInfo["title"],ENT_NOQUOTES,'utf-8')."</a>"; ?></td>
	</tr>
    
	<tr>
		<th>URL:</th>
		<td><?php echo "<a href='".htmlspecialchars($dictInfo["og_url"], ENT_NOQUOTES,'utf-8')."'>".htmlspecialchars($dictInfo["og_url"],ENT_NOQUOTES,'utf-8')."</a>"; ?></td>
	</tr>	
    
	<tr>
		<th>ID:</th>
		<td><?php echo htmlspecialchars($dictInfo["id"],ENT_NOQUOTES,'utf-8'); ?></td>
	</tr>
    
	<tr>
		<th>Description:</th>
		<td><?php echo htmlspecialchars($dictInfo["og_description"],ENT_NOQUOTES,'utf-8'); ?></td>
	</tr>
	 </table>
</li>

<?php
 }
?>

</ol>

<?php
}
?>
 	</body>
</html>