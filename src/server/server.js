var net = require ( 'net' ) ;
var HOST = '140.112.30.52' ; // parameterize the IP of the Listen 
var PORT = 5566 ; // TCP LISTEN port 

const CONNECT = '00'
const REGISTER = '01'
const LOGIN = '02'
const LOGOUT = '03'

const CONNECT_OK = '10'
const CONNECT_FAIL = '11'

const REGISTER_OK = '20'
const REGISTER_DUP = '21'
const REGISTER_ILE = '22'
const REGISTER_FAIL = '23'

const LOGIN_OK = '30'
const LOGIN_IDNF = '31'
const LOGIN_WRPS = '32'
const LOGIN_ALRD = '33'
const LOGIN_FAIL = '34'

const LOGOUT_OK = '40'
const LOGOUT_FAIL = '41'

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
			ret = CONNECT_OK;
			console.log( msg.split(/[\/,\n]+/)[0] );
		}
		else if(type === REGISTER){
			var strs = msg.split(/[\/,\n]+/);
			var _id = strs[0];
			var _pass = strs[1];
			ret = REGISTER_OK;
			console.log( 'register( ' + _id + ', ' + _pass + ' )' );
		}
		else if(type === LOGIN){
			if(IsLoggedIn){
				ret = LOGIN_ALRD;
				console.log( 'current user: ' + id );
			}
			else{
				var strs = msg.split(/[\/,\n]+/);
				id = strs[0];
				pass = strs[1];
				IsLoggedIn = true;
				ret = LOGIN_OK;
				console.log( 'login( ' + id + ', ' + pass + ' )' );
			}
		}
		else if(type === LOGOUT){
			IsLoggedIn = false;
			ret = LOGOUT_OK;
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
