<?php

$raw = $_GET['term'];

$formatted = htmlspecialchars($raw, ENT_QUOTES, 'utf-8'); # Probably not necessary


# Only suggest based on last word
$as_array = explode(" ", trim($formatted)); 

$prev = "";
$recent = "";

if(count($as_array) == 1){
	$recent = $as_array[0];
}
else{
	$prev = implode(" ", array_slice($as_array, 0, -1));
	$recent = end($as_array);
}


$lookup = "http://localhost:8983/solr/myexample/suggest?q=".$recent;

$results = file_get_contents($lookup);

# Fix format
$decode = json_decode($results);


$recs = array();

$suggestions = $decode->suggest->suggest->$recent->suggestions;

foreach ($suggestions as $s) {
	array_push($recs, $prev." ".$s->term);
}

# Fix format
echo json_encode($recs);

?>