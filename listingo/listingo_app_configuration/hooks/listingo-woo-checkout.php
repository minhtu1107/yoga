<?php 
/* Template Name: Android Checkout Page 

* Plugin URI: Plugin URI: http://androidecommerce.com/
 * Description: A wordpress Plugin for androidecommerce.com  app template  
 * Author: vectorcoder
 * Author URI: http://vectorcoder.com
*/ 
 if(isset($_GET['order_id'])){ 

   $oid = $_GET['order_id'];
	  $get_data_id    =  "SELECT * FROM `".LISTINGO_APP_TEMP_CHCKOUT."`  where `id`=".$oid;
	 $all_get_link   = $wpdb->get_results($get_data_id);
	 $data_get_lnk   = $all_get_link[0]->data_link;


	$order_data = urldecode($data_get_lnk);
	$order_data = "[".stripslashes($data_get_lnk)."]";
	$order_data = json_decode($order_data,false);
		//echo "<pre>";
		//print_r($order_data);
		//echo "</pre>";
	if ($order_data):
	global $woocommerce;
	$checkout_url = wc_get_checkout_url();
	foreach($order_data[0] as $key => $value){
    $$key = $value;
  }	
	
	if(isset($payment_method) && $payment_method != ""){
		$payment_method_select   = $payment_method;
	}
	
// 	if(isset($token) && $token != ""){
// 	  	$user_cookie   = $token;
// 	 $user_login_id = wp_validate_auth_cookie($user_cookie , 'logged_in');
// 	if(isset($user_login_id)){
// 		$user_log = get_userdata($user_login_id);
// 		if (!is_user_logged_in()) {
//             wp_set_current_user($user_login_id, $user_log->user_login);
//             wp_set_auth_cookie($user_login_id);
//             $url = $_SERVER['REQUEST_URI'];
//             header("Refresh: 0; url=$url");
//         }		
// 	}
// 	else{ echo "Invalid User cookie , Please Login again"; return;}
// }	


	$woocommerce->session->set('refresh_totals', true);
    $woocommerce->cart->empty_cart();
	
	foreach ($products as $single_item) {
        $product_id 	= absint($single_item->product_id);
        $quantity 		= absint($single_item->quantity);
		if(isset($single_item->variation_id)){		
        	$is_variable 	= absint($single_item->variation_id);
		}else{ $is_variable = "";}
        if (isset($is_variable) && $is_variable >= 1 ) {
            $variable_pro 	 = new WC_Product_Variable($product_id);
            $all_variations  = $variable_pro->get_available_variations();
            foreach ($all_variations as $single_variation => $single_value) {
                if ($single_value['variation_id']  ==  $is_variable ) {
                    $attribute = $single_value['attributes'];
                    $woocommerce->cart->add_to_cart($product_id, $quantity, $is_variable, $attribute);
                }
            }
        } 
		else {
            $woocommerce->cart->add_to_cart($product_id, $quantity);
        }
    }
	if (isset($coupons)) {
        foreach ($coupons as $coupon) {
            $woocommerce->cart->add_discount($coupon->code);
        }
    }
    $shipping_methods 	= '';
    if (isset($shipping_ids)) {
		foreach($shipping_ids as $shipm_id){
			 $shipid	       = $shipm_id->ship_id;
			 $shipping_methods = $shipm_id->method_id;						
		}

		 $woocommerce->session->set( 'chosen_shipping_methods', array($shipping_methods.":".$shipid));  
		 $woocommerce->session->set( 'chosen_payment_method', $payment_method_select );  
    }
?>
<!doctype html>
<html>
<head>
	<meta charset="<?php bloginfo( 'charset' ); ?>">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="profile" href="http://gmpg.org/xfn/11">
	<title>Mobile Checkout</title>
	<?php wp_head(); ?>
</head>

<body <?php body_class(); ?>>
<div style="display:none;">
  <form name="checkout" id="my_chckout" method="post" class="checkout woocommerce-checkout" action="<?php echo $checkout_url."?mob=true&one_page=".$one_page."&platform=".$platform; ?>" enctype="multipart/form-data">
  <input type="text" class="input-text " name="billing_first_name" id="billing_first_name" placeholder="" value="<?php echo $billing_info->first_name; ?>"/>
  <input type="text" class="input-text " name="billing_last_name" id="billing_last_name" placeholder="" value="<?php echo $billing_info->last_name; ?>"/>
  <input type="text" class="input-text " name="billing_company" id="billing_company" placeholder="" value="<?php echo $billing_info->company; ?>" />
  <input type="text" class="input-text " name="billing_country" id="billing_country" placeholder="" value="<?php echo $billing_info->country; ?>"/>
  <input type="text" class="input-text " name="billing_address_1" id="billing_address_1" placeholder="House number and street name" value="<?php  echo $billing_info->address_1; ?>" />
  <input type="text" class="input-text " name="billing_address_2" id="billing_address_2" placeholder="Apartment, suite, unit etc. (optional)" value="<?php  echo $billing_info->address_2; ?>" />
  <input type="text" class="input-text " name="billing_city" id="billing_city" placeholder="" value="<?php  echo $billing_info->city; ?>" />
  <input type="text" class="input-text " value="<?php  echo $billing_info->state; ?>" placeholder="" name="billing_state" id="billing_state" />
  <input type="text" class="input-text " name="billing_postcode" id="billing_postcode" placeholder="" value="<?php  echo $billing_info->postcode; ?>" />
  <input type="tel" class="input-text " name="billing_phone" id="billing_phone" placeholder="" value="<?php  echo $billing_info->phone; ?>" />
  <input type="email" class="input-text " name="billing_email" id="billing_email" placeholder="" value="<?php  echo $billing_info->email; ?>" />
  <input id="ship-to-different-address-checkbox" class="woocommerce-form__input woocommerce-form__input-checkbox input-checkbox"  type="checkbox" name="ship_to_different_address" value="1" <?php if(isset($sameAddress) && $sameAddress !=""){?> checked="checked" <?php } ?>>    
  <input type="text" class="input-text " name="shipping_first_name" id="shipping_first_name" placeholder="" value="<?php  echo $shipping_info->first_name; ?>" />  <input type="text" class="input-text " name="shipping_last_name" id="shipping_last_name" placeholder="" value="<?php  echo $shipping_info->last_name; ?>" />  <input type="text" class="input-text " name="shipping_company" id="shipping_company" placeholder="" value="<?php  echo $shipping_info->company; ?>" />  <input type="text" class="input-text" name="shipping_country" id="shipping_country" placeholder="" value="<?php  echo $shipping_info->country; ?>"/>            
  <input type="text" class="input-text " name="shipping_address_1" id="shipping_address_1" placeholder="House number and street name" value="<?php  echo $shipping_info->address_1; ?>" />            
  <input type="text" class="input-text " name="shipping_address_2" id="shipping_address_2" placeholder="Apartment, suite, unit etc. (optional)" value="<?php  echo $shipping_info->address_2; ?>" />             
  <input type="text" class="input-text " name="shipping_city" id="shipping_city" placeholder="" value="<?php  echo $shipping_info->city; ?>" />
  <input type="text" class="input-text " value="<?php  echo $shipping_info->state; ?>" placeholder="" name="shipping_state" id="shipping_state" />
  <input type="text" class="input-text " name="shipping_postcode" id="shipping_postcode" placeholder="" value="<?php  echo $shipping_info->postcode; ?>" />  <textarea name="order_comments" class="input-text " id="order_comments" placeholder="Notes about your order, e.g. special notes for delivery." rows="2" cols="5"><?php $order_data['customer_note']; ?></textarea>      
<input type="radio" checked="checked" class="shipping_method" name="shipping_method[]" id="shipping_method_0_<?php echo $shipping_methods; ?><?php echo $shipid; ?>" value="<?php echo  $shipping_methods; ?>:<?php echo $shipid; ?>" /><?php echo $shipping_methods; ?>                  
  </form>
 </div>               
<script type="text/javascript"> setTimeout(function(){document.getElementById("my_chckout").submit();}, 1000);</script>
</body>
</html>
<?php endif; } ?>