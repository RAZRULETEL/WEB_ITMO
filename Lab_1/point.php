<?php
ini_set('display_errors', 1);

ini_set('display_startup_errors', 1);

error_reporting(E_ALL);

$startTime = hrtime(true);
$points = "points.txt";
if(isset($_GET['clear'])){
	exit(file_put_contents($points, ""));
}else if(isset($_GET['list'])){
	if(file_exists($points))
		echo "[".join(",", explode("\n", file_get_contents($points), -1))."]";
	else
		echo "[]";
} else if(!isset($_GET['x']) || !isset($_GET['y']) || !isset($_GET['radius']))
	echo "Не все данные введены, требуются следующие параметры: x, y, radius";
else {
	$x = validateX($_GET['x']);
	$y = validateY($_GET['y']);
	$radius = validateRadius($_GET['radius']);
	if(is_string($x))
		echo "x: ".$x;
	else if(is_string($y))
		echo "y: ".$y;
	else if(is_string($radius))
		echo "radius: ".$radius;
	else {
		$result = '{"x":'.$x.', "y":'.$y.', "r":'.$radius.', "success":'.json_encode(checkDot($x, $y, $radius)).', "timestamp":'.(microtime(true)*1000).', "exec_time":'.((hrtime(true) - $startTime)/1e+6).'}';
		file_put_contents($points, $result."\n", FILE_APPEND);
		echo $result;
	}
}


function validateNumber($number, $min, $max){
	 if(is_numeric(str_replace(",", ".", $number))){
		$number = (float)str_replace(",", ".", $number);
		if($number >= $min && $number <= $max)
			return $number;
		return "значение выходит за границы [".$min.", ".$max."]";
	}else
		return "введённое значение не является числом";
}

function validateX($x){
	$x = validateNumber($x, -3, 5);
	if(!is_numeric($x))
		return $x;
	if(($x % 1) == 0)
		return $x;
	else
		return "недопустимое значение, должно быть кратно 1";
}

function validateY($y){
	return validateNumber($y, -5, 5);
}

function validateRadius($r){
	$r = validateNumber($r, 1, 3);
	if(!is_numeric($r))
		return $r;
	if(($r * 2 % 1) == 0)
		return $r;
	else
		return "недопустимое значение, должно быть кратно 0,5";
}

function checkDot($x, $y, $r){
	if($x < 0){
		if($y > 0)
			return sqrt($x * $x + $y * $y) <= $r;//Arc
		else
			return False; 
	}else
		if($y > 0)
			return ($x + $y) <= $r / 2;//Triangle
		else
			return $x <= $r / 2 && $y >= -$r;//Rect
}
 ?>
