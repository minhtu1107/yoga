<?php

/**
 * Fired during plugin activation
 *
 * @link       https://themeforest.net/user/themographics/portfolio
 * @since      1.0.0
 *
 * @package    ListingoApp
 * @subpackage ListingoApp/includes
 */

/**
 * Fired during plugin activation.
 *
 * This class defines all code necessary to run during the plugin's activation.
 *
 * @since      1.0.0
 * @package    Elevator
 * @subpackage ListingoApp/includes
 * @author     Themographics <themographics@gmail.com>
 */
class ListingoApp_Activator {

	/**
	 * Short Description. (use period)
	 *
	 * Long Description.
	 *
	 * @since    1.0.0
	 */
	public static function activate() {
			global $json_api ,$wpdb;
		 $post_table = $wpdb->prefix . "posts";
		 $meta_table = $wpdb->prefix . "postmeta";
		 $check_page_exist = "select * from `".$post_table."`as page_rec INNER JOIN `".$meta_table."` as meta_rec on page_rec.ID = meta_rec.post_id where `post_type`='page' && `post_status`='publish' && (meta_key = '_wp_page_template' && meta_value = 'listingo-woo-checkout.php')";
		 $have_page_record = $wpdb->get_results($check_page_exist);
		
		if($have_page_record == "" || $have_page_record == null ){
			$addd_page = "insert into `".$post_table."` set `post_type`='page' , `post_title`='Android Mobile Checkout' , `post_name`='listingo-app-checkout' , `guid`='".site_url()."/listingo-app-checkout' , `post_status`='publish' , `post_author`=1 , `ping_status`='closed' , `comment_status`='closed' , `menu_order`=0 , `post_date`='".date('Y-m-d H:i:s')."' , `post_date_gmt`='".date('Y-m-d H:i:s')."'";
			if($wpdb->query($addd_page)){
				$inserted_page_id  = $wpdb->insert_id; 
				update_post_meta($inserted_page_id,'_wp_page_template','listingo-woo-checkout.php'); 
			}			
		}


	$sql_data_android = "CREATE TABLE IF NOT EXISTS `" . LISTINGO_APP_TEMP_CHCKOUT . "` (
	  `id` int(11) NOT NULL auto_increment,
	  `data_link` text NOT NULL,
	  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	   PRIMARY KEY (`id`)
	)ENGINE=MyISAM DEFAULT CHARSET=utf8;";


	require_once( ABSPATH . 'wp-admin/includes/upgrade.php' );	
	
	
	if($wpdb->get_var("SHOW TABLES LIKE '".LISTINGO_APP_TEMP_CHCKOUT."'") != LISTINGO_APP_TEMP_CHCKOUT) 
	{
		$wpdb->query($sql_data_android);
	}

	}




}


