var net = require ( 'net' ) ;
var HOST = '140.112.30.52' ; // parameterize the IP of the Listen 
var PORT = 5566 ; // TCP LISTEN port 

const CONNECT	= '00'
const REGISTER	= '01'
const LOGIN		= '02'
const LOGOUT	= '03'

const OK		= '10'
const FAIL		= '11'

var IsLoggedIn = false;
var id;
var pass;

net.createServer ( function ( sock ) { 
	console.log ( 'CONNECTED: '+sock.remoteAddress +':'+ sock.remotePort ) ;
   
	sock.on ( 'data', function ( data ) { 
		var packet = data.toString();
		var type = packet.substring(0,2);
		var msg = packet.substring(3);
		var ret = '';
		
		if(type === CONNECT){
			ret = OK;
			console.log( msg.split(/[\/,\n]+/)[0] );
		}
		else if(type === REGISTER){
			var strs = msg.split(/[\/,\n]+/);
			var _id = strs[0];
			var _pass = strs[1];
			ret = OK;
			console.log( 'register( ' + _id + ', ' + _pass + ' )' );
		}
		else if(type === LOGIN){
			if(IsLoggedIn){
				ret = FAIL;
				console.log( 'current user: ' + id );
			}
			else{
				var strs = msg.split(/[\/,\n]+/);
				id = strs[0];
				pass = strs[1];
				IsLoggedIn = true;
				ret = OK;
				console.log( 'login( ' + id + ', ' + pass + ' )' );
			}
		}
		else if(type === LOGOUT){
			IsLoggedIn = false;
			ret = OK;
			console.log( 'logout( ' + id + ', ' + pass + ' )' );
		}

		sock.write ( ret ) ; 
	} ) ; 
	
	sock.on ( 'close', function ( data ) { 
		IsLoggedIn = false;
		console.log ( 'CLOSED: '+sock.remoteAddress +' '+ sock.remotePort ) ;
   	} ) ; 
} ) .listen ( PORT, HOST ) ;

console.log ( 'Server listening on ' + HOST +':'+ PORT ) ;
