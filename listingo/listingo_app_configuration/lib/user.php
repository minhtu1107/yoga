<?php

    if (!class_exists('ListingoApp_User_Route')) {

        class ListingoApp_User_Route extends WP_REST_Controller
        {

            /**
             * Register the routes for the objects of the controller.
             */
            public function register_routes() {
                $version = '1';
                $namespace = 'api/v' . $version;
                $base = 'user';

                register_rest_route($namespace, '/' . $base . '/login',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_items'),
                        'args' => array(
                        ),
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'user_login'),
                        'args' => array(),
                    ),
                        )
                );

                register_rest_route($namespace, '/' . $base . '/profile/(?P<id>\d+)',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_profile_date'),
                         'args' => [
                            'id'
                        ],
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'set_profile_date'),
                        'args' => array(),
                    ),
                        )
                );

                 register_rest_route($namespace, '/' . $base . '/token',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_items'),
                        'args' => array(
                        ),
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'save_user_device_token'),
                        'args' => array(),
                    ),
                        )
                );
                 register_rest_route($namespace, '/' . $base . '/remove-token',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_items'),
                        'args' => array(
                        ),
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'remove_user_device_token'),
                        'args' => array(),
                    ),
                        )
                );


                register_rest_route($namespace, '/' . $base . '/reset-password',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_items'),
                        // 'permission_callback' => array( $this, 'get_items_permissions_check' ),
                        'args' => array(
                        ),
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'forgot_password'),
                        //'permission_callback' => array( $this, 'create_item_permissions_check' ),
                        'args' => array(),
                    ),
                        )
                );

                register_rest_route($namespace, '/' . $base . '/appointments',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_my_appoinments'),
                        // 'permission_callback' => array( $this, 'get_items_permissions_check' ),
                        'args' => array(
                        ),
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'create_item'),
                        //'permission_callback' => array( $this, 'create_item_permissions_check' ),
                        'args' => array(),
                    ),
                ));

                 register_rest_route($namespace, '/' . $base . '/services',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_my_services'),
                        // 'permission_callback' => array( $this, 'get_items_permissions_check' ),
                        'args' => array(
                        ),
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'set_my_services'),
                        //'permission_callback' => array( $this, 'create_item_permissions_check' ),
                        'args' => array(),
                    ),
                ));


                 register_rest_route($namespace, '/' . $base . '/favorites',
                        array(
                    array(
                        'methods' => WP_REST_Server::READABLE,
                        'callback' => array($this, 'get_my_favorites'),
                        // 'permission_callback' => array( $this, 'get_items_permissions_check' ),
                        'args' => array(
                        ),
                    ),
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'set_my_favorites'),
                        //'permission_callback' => array( $this, 'create_item_permissions_check' ),
                        'args' => array(),
                    ),
                ));



                 register_rest_route($namespace, '/' . '/listing-checkout',
                        array(
                    array(
                        'methods' => WP_REST_Server::CREATABLE,
                        'callback' => array($this, 'create_checkout_page'),
                        //'permission_callback' => array( $this, 'create_item_permissions_check' ),
                        'args' => array(),
                    ),
                ));


            }
            public function create_checkout_page($request){
                global $wpdb;
                $params = $request->get_params();       

                  $insert_data_link = "insert into `".LISTINGO_APP_TEMP_CHCKOUT."` set `data_link`='".json_encode($params['checkout_json'])."'";     
                $wpdb->query($insert_data_link);
                if(isset($wpdb->insert_id)){ 
                    $data_id = $wpdb->insert_id; 
                } else{
                    $data_id = $wpdb->print_error();
                }

                $json['type'] = "success";
                $json['message'] = esc_html__(".", "listingo_core");
                $json['data_id'] = $data_id;
                return new WP_REST_Response($json, 200);

            }
            /**
             * Get a collection of items
             *
             * @param WP_REST_Request $request Full data about the request.
             * @return WP_Error|WP_REST_Response
             */
            public function get_items($request) {
                $items['data'] = array();
                return new WP_REST_Response($items, 200);
            }

            public function get_my_services($request) {
                $user_id = $_GET['user_id'];
                $services  =  get_user_meta($_GET['user_id'], 'profile_services', true);
                //print_r($services);
                //$items = unserialize($services);

                 $items = array();
                if( !empty($services )){
                    $items = array_values($services);    
                }
                return new WP_REST_Response($items, 200);
            }




            /**
             * Get a collection of items
             *
             * @param WP_REST_Request $request Full data about the request.
             * @return WP_Error|WP_REST_Response
             */
            public function get_profile_date($request) {
                


                    $ID = $request['id'];
                    $user = get_user_by( 'ID', $ID  );
                    $user_meta =  get_user_meta($ID);
                    //print_r($user_meta);die;
    //          return $user_meta;


                    $data = array();
                    $data['ID'] = $user->data->ID ;
                    $data['user_nicename'] = $user->data->user_nicename ;
                    $data['user_email'] = $user->data->user_email ;
                    $data['user_url'] = $user->data->user_url ;
                    $data['user_status'] = $user->data->user_status ;
                    $data['display_name'] = $user->data->display_name ;

        

                    $data['nickname'] = empty($user_meta['nickname'][0])? '' : $user_meta['nickname'][0];
                    $data['first_name'] = empty($user_meta['first_name'][0])? '' : $user_meta['first_name'][0];
                    $data['last_name'] = empty($user_meta['last_name'][0])? '' : $user_meta['last_name'][0];
                    $data['description'] = empty($user_meta['description'][0])? '' : $user_meta['description'][0];
                    $data['comment_shortcuts'] = empty($user_meta['comment_shortcuts'][0])? '' : $user_meta['comment_shortcuts'][0];
                    $data['locale'] = empty($user_meta['locale'][0])? '' : $user_meta['locale'][0];
                    $data['verify_user'] = empty($user_meta['verify_user'][0])? '' : $user_meta['verify_user'][0];
                    $data['full_name'] = empty($user_meta['full_name'][0])? '' : $user_meta['full_name'][0];
                    $data['activation_status'] = empty($user_meta['activation_status'][0])? '' : $user_meta['activation_status'][0];
                    $data['profile_photo'] = empty($user_meta['profile_photo'][0])? '' : $user_meta['profile_photo'][0];
                    $data['profile_banner'] = empty($user_meta['profile_banner'][0])? '' : $user_meta['profile_banner'][0];
                    $data['profile_appointment'] = empty($user_meta['profile_appointment'][0])? '' : $user_meta['profile_appointment'][0];
                    $data['profile_contact'] = empty($user_meta['profile_contact'][0])? '' : $user_meta['profile_contact'][0];
                    $data['profile_hours'] = empty($user_meta['profile_hours'][0]) ? '' :$user_meta['profile_hours'][0];
                    $data['profile_service'] = empty($user_meta['profile_service'][0])? '' : $user_meta['profile_service'][0];
                    $data['profile_team'] = empty($user_meta['profile_team'][0])? '' : $user_meta['profile_team'][0];
                    $data['profile_gallery'] = empty($user_meta['profile_gallery'][0])? '' : $user_meta['profile_gallery'][0];
                    $data['profile_videos'] = empty($user_meta['profile_videos'][0])? '' : $user_meta['profile_videos'][0];
                    $data['company_name'] = empty($user_meta['company_name'][0])? '' : $user_meta['company_name'][0];
                    $data['zip'] = empty($user_meta['zip'][0])? '' : $user_meta['zip'][0];
                    $data['tag_line'] = empty($user_meta['tag_line'][0])? '' : $user_meta['tag_line'][0];
                    $data['phone'] = empty($user_meta['phone'][0])? '' : $user_meta['phone'][0];
                    $data['city'] = empty($user_meta['city'][0])?'':$user_meta['city'][0];
                    $data['country'] = empty($user_meta['country'][0])?'':$user_meta['country'][0];
                    $data['fax'] = empty($user_meta['fax'][0])? '' : $user_meta['fax'][0];
                    $data['privacy_settings'] = unserialize($user_meta['privacy_settings'][0]) ?  unserialize($user_meta['privacy_settings'][0]) : new stdClass();
                    
                    if( !empty( $user_meta['privacy'][0] )){
                        $data['privacy'] = unserialize($user_meta['privacy'][0]) ? unserialize($user_meta['privacy'][0]) : new stdClass();    
                    }else{
                        $data['privacy'] = new stdClass();    
                    }
                    
                    $data['address'] = empty($user_meta['address'][0])? '' : $user_meta['address'][0];
                    $data['facebook'] = empty($user_meta['facebook'][0])? '' : $user_meta['facebook'][0];
                    $data['twitter'] = empty($user_meta['twitter'][0])? '' : $user_meta['twitter'][0];
                    $data['linkedin'] = empty($user_meta['linkedin'][0])? '' : $user_meta['linkedin'][0];
                    $data['pinterest'] = empty($user_meta['pinterest'][0])? '' : $user_meta['pinterest'][0];
                    $data['google_plus'] = empty($user_meta['google_plus'][0])?'': $user_meta['google_plus'][0];
                    $data['instagram'] = empty($user_meta['instagram'][0])? '' : $user_meta['instagram'][0];
                    $data['tumblr'] = empty($user_meta['tumblr'][0])? '' : $user_meta['tumblr'][0];
                    $data['skype'] = empty($user_meta['skype'][0])? '' : $user_meta['skype'][0];
                    $data['last_update'] = empty($user_meta['last_update'][0])? '' : $user_meta['last_update'][0];
                    $data['phone_number'] = empty($user_meta['phone_number'][0])? '' : $user_meta['phone_number'][0];
                    $data['email'] = empty($user_meta['email'][0])? '' : $user_meta['email'][0];
                    $data['_category_id'] = ( $user_meta['category'][0]);    
                    if( strlen( $user_meta['category'][0]) < 6  ){
                        //echo $user_meta['category'][0];die;
                        $data['category'] = get_the_title($user_meta['category'][0]);  
                    }else{
                        $data['category'] = $user_meta['category'][0];    
                    }
                    $data['category_id'] = ( html_entity_decode($user_meta['category'][0]));    
                     $data['sub_category_id'] = $user_meta['sub_category'][0];
                     $data['sub_category'] = array();
                    
                    
                    if( !empty($user_meta['sub_category'][0])){
                        if(is_serialized($user_meta['sub_category'][0]) ){
                                $data['sub_category'] = unserialize( $user_meta['sub_category'][0] );
                        }else{
                             $data['sub_category'] = array(($user_meta['sub_category'][0]) );

                        }
                            
                        //if( !is_string($user_meta['sub_category'][0])){
                        // if( preg_match( '/a\:1/', $user_meta['sub_category'][0])  ){

                        //     $data['sub_category'] = !empty(unserialize( $user_meta['sub_category'][0] )) ? unserialize($user_meta['sub_category'][0]) :array();        
                        // }else{
                            
                        //     $data['sub_category'] = array($user_meta['sub_category'][0]);
                        // }
                    
                    }else{
                        $data['sub_category'] = array();
                    }
                    $data['latitude'] = $user_meta['latitude'][0];
                    $data['longitude'] = $user_meta['longitude'][0];
                $data['professional_statements'] = empty($user_meta['professional_statements'][0])?'':$user_meta['professional_statements'][0];
         
         

                  $data['subscription_featured_expiry'] = $user_meta['subscription_featured_expiry'][0];
                    if(isset($user_meta['subscription_featured_expiry'][0])){
                        $data['subscription_featured_expiry_preview'] = date('Y-m-d H:i:s', $user_meta['subscription_featured_expiry'][0]);    
                    }
                    
                    $data['subscription_expiry'] = $user_meta['subscription_expiry'][0];
                    $data['subscription_id'] = $user_meta['subscription_id'][0];

                  if( !empty($user_meta['sp_subscription'][0])){
                        if(is_serialized($user_meta['sp_subscription'][0]) ){
                                $data['sp_subscription'] = unserialize( $user_meta['sp_subscription'][0] );
                        }
                    }else{
                         $data['sp_subscription'] = array();
                    }

                   // $data['sp_subscription'] = empty($user_meta['sp_subscription'][0]) ? new stdClass() : $user_meta['sp_subscription'][0];
         
            $user_banner = apply_filters(
            'listingo_get_media_filter', listingo_get_user_banner(array('width' => 0, 'height' => 0), $user->data->ID), array('width' => 1920, 'height' => 380) //size width,height
    );
      
        $data['bannerObject']= new stdClass();    
        if(is_serialized($user_meta['profile_banner'][0]) ){
            $data['profile_banner_object'] = empty( $user_meta['profile_banner'][0] ) ? array(): array_values(  unserialize($user_meta['profile_banner'][0])['image_data']);
            
        }    
        
        $data['avatarObject']= new stdClass();    
        if($user_meta['profile_avatar'] != null &!empty($user_meta['profile_avatar'][0]) && is_serialized($user_meta['profile_avatar'][0]) ){
            $data['profile_avatar_object'] = empty( $user_meta['profile_avatar'][0] ) ? array() : array_values(  unserialize($user_meta['profile_avatar'][0])['image_data']);
            
        }    
                    

        $data['profile_banner'] = empty($user_meta['profile_banner'][0]) ? '' :$user_meta['profile_banner'][0] ;
                    
                    
    if(  !empty( $data['profile_languages']  )){
                    $data['profile_languages'] =  unserialize($user_meta['profile_languages'][0] ) ?  unserialize($user_meta['profile_languages'][0] ) : array(); 
}else{
    $data['profile_languages']  = array();
}
    $data['avatar_id'] =
                      $data['avatar'] = apply_filters(
                            'listingo_get_media_filter',
                            listingo_get_user_avatar(array('width' => 400, 'height' => 400),
                                    $user->data->ID),
                            array('width' => 400, 'height' => 400)
                    );

                    $data['banner'] = apply_filters(
                            'listingo_get_media_filter',
                            listingo_get_user_banner(
                                array('width' => 540, 'height' => 240), 
                                $user->data->ID),array('width' => 540, 'height' => 240)
                                    //size width,height
                    );



                      //  $data[ 'meta'] = $user_meta; 



                $items =( $data );
                return new WP_REST_Response($items, 200);


    // {
    //  "username": "Fyberlink Networks Limited",
    //  "avatar": "https://fitsach.com/wp-content/uploads/2018/05/logo-1.jpg",
    //  "banner": "https://fitsach.com/wp-content/uploads/2018/05/logo-1.jpg",
    //  "email": "fyberlinknetworks@gmail.com",
    //  "website": "http://cynet.tk",
    //  "full_name": "Fyberlink Networks Limited",
    //  "nickname": "Jeff Faiba",
    //  "company_name": "Fyberlink Networks Limited",
    //  "address": "",
    //  "tag_line": "",
    //  "fax": "",
    //  "country": "",
    //  "city": "",
    //  "description": "",
    //  "first_name": "Jeff",
    //  "last_name": "Faiba",
    //  "professional_statements": "",
    //  "facebook": "",
    //  "twitter ": "",
    //  "linkedin": "",
    //  "pinterest": "",
    //  "google_plus": "",
    //  "tumblr": "",
    //  "instagram": "",
    //  "skype": "",
    //  "latitude": "-1.28333",
    //  "longitude": " 36.81667",
    //  "phone": "0724569618",
    //  "profile_languages": [],
    //  "zip": ""
    // }



            }
            public function get_my_favorites($request) {

                $user_id = $_GET['user_id'];
                $favorites  =  get_user_meta($_GET['user_id'], 'wishlist', true);
                $items = array();
                if( empty($favorites) ){
                    $items =  array();
                    return new WP_REST_Response($items, 200);
                    exit;

                }
                //Verify user
                $meta_query_args[] = array(
                    'key' => 'verify_user',
                    'value' => 'on',
                    'compare' => '='
                );
                //active users filter
                $meta_query_args[] = array(
                    'key' => 'activation_status',
                    'value' => 'active',
                    'compare' => '='
                );

                $query_args['include'] =  $favorites ; 
                if (!empty($meta_query_args)) {
                    $query_relation = array('relation' => 'AND',);
                    $meta_query_args = array_merge($query_relation, $meta_query_args);
                    $query_args['meta_query'] = $meta_query_args;
                }

                
                //Count total users for pagination
                $total_query = new WP_User_Query($query_args);
                $total_users = $total_query->total_users;
                $query_args['number'] = $limit;
                $query_args['offset'] = $offset;

                $default_view = 'list';
                if (function_exists('fw_get_db_post_option')) {
                    $default_view = fw_get_db_settings_option('dir_search_view');
                }

                $user_query = new WP_User_Query($query_args);
                $sp_userslist['status'] = 'none';
                $sp_usersdata = array();
                $sp_userslist = array();

                if (!empty($user_query->results)) {

                    $sp_userslist['status'] = 'found';

                    if (!empty($sp_category)) {
                        $title = get_the_title($sp_category);
                        $postdata = get_post($sp_category);
                        $slug = $postdata->post_name;
                    } else {
                        $title = '';
                        $slug = '';
                    }

                    foreach ($user_query->results as $user) {

                        if (isset($media_type) && $media_type === 'banner') {
                            $thumb = apply_filters(
                                    'listingo_get_media_filter', listingo_get_user_banner(array('width' => 370, 'height' => 270), $user->ID), array('width' => 370, 'height' => 270)
                            );
                        } else {
                            $thumb = apply_filters(
                                    'listingo_get_media_filter', listingo_get_user_avatar(array('width' => 370, 'height' => 270), $user->ID), array('width' => 370, 'height' => 270)
                            );
                        }

                        $item['category_id'] = get_user_meta($user->ID, 'category', true);
                        $item['category'] = get_the_title($user->category);
                        $item['ID'] = $user->ID;
                        $item['latitude'] = $user->latitude;
                        $item['longitude'] = $user->longitude;
                        $item['phone'] = $user->phone;
                        $username = listingo_get_username($user->ID);
                        $item['username'] = $username;
                        $item['avatar'] = $thumb;


                        $item['wp_capabilities'] = $user->wp_capabilities;
                        $item['wp_user_level'] = $user->wp_user_level;
                        $item['usertype'] = $user->usertype;

                        $item['email'] = $user->user_email;
                        $item['website'] = $user->user_url;


                        $item['full_name'] = $user->full_name;
                        $item['nickname'] = $user->nickname;
                        $item['r_id'] = $user->r_id;
                        //$item['category'] = $user->category;
                        $item['sub_category'] = $user->sub_category;
                        $item['company_name'] = $user->company_name;
                        $item['profile_avatar'] = empty($user->profile_avatar) ? new stdClass() : $user->profile_avatar;
                        if (!empty($user->profile_gallery_photos)) {
                            $a = $user->profile_gallery_photos;
                            $a['image_data'] = array_values($a['image_data']);
                        } else {
                            $a['image_data'] = array();
                        }

                        $db_category_type = get_user_meta($user->ID, 'category', true);


                        /* Get the rating headings */
                        $rating_titles = $this->listingo_get_reviews_evaluation($db_category_type, 'leave_rating');
                        $item['rating_titles'] = $rating_titles;

                        $item['profile_gallery_photos'] = empty($user->profile_gallery_photos) ? new stdClass() : ( $a);
                        $item['facebook'] = $user->facebook;
                        $item['twitter '] = $user->twitter;
                        $item['linkedin'] = $user->linkedin;
                        $item['pinterest'] = $user->pinterest;
                        $item['google_plus'] = $user->google_plus;
                        $item['tumblr'] = $user->tumblr;
                        $item['instagram'] = $user->instagram;
                        $item['skype'] = $user->skype;
                        $item['activation_status'] = $user->activation_status;
                        $item['address'] = $user->address;
                        $item['latitude'] = $user->latitude;
                        $item['longitude'] = $user->longitude;
                        $item['tag_line'] = $user->tag_line;
                        $item['phone'] = $user->phone;
                        $item['fax'] = $user->fax;
    //                    $item['profile_languages'] = empty($user->profile_languages) ? array() : $user->profile_languages;
    if( is_array( $user->profile_languages)) {
                        $item['profile_languages'] = empty($user->profile_languages) ? array() :($user->profile_languages);
    }else{
                       $item['profile_languages'] = is_string($user->profile_languages) && !strlen($user->profile_languages) ? array() :array($user->profile_languages);
      
    }
                        $item['zip'] = $user->zip;
                        $item['verify_user'] = $user->verify_user;
                        $item['privacy'] = empty($user->privacy) ? new stdClass() : $user->privacy;
                        $item['country'] = $user->country;
                        $item['city'] = $user->city;
                        $item['description'] = $user->description;
                        $item['first_name'] = $user->first_name;
                        $item['last_name'] = $user->last_name;
                        $item['professional_statements'] = $user->professional_statements;
                        $item['subscription_featured_expiry'] = $user->subscription_featured_expiry;
                        $item['subscription_expiry'] = $user->subscription_expiry;
                        $item['subscription_id'] = $user->subscription_id;
                        $item['sp_subscription'] = empty($user->sp_subscription) ? new stdClass() : $user->sp_subscription;
                        $item['awards'] = empty($user->awards) ? array() : $user->awards;
                        $item['business_hours'] = empty($user->business_hours) ? new stdClass() : $user->business_hours;
                        $item['business_hours_format'] = $user->business_hours_format;
                        $item['default_slots'] = empty($user->default_slots) ? new stdClass() : $user->default_slots;
                        $item['experience'] = empty($user->experience) ? array() : $user->experience ;
                        $item['profile_brochure'] = $user->profile_brochure;
                        $item['profile_insurance'] = empty($user->profile_insurance) ? array() : $user->profile_insurance;
                        $amenities = $user->profile_amenities;
                        $item['isfav'] = true;    

                        $_amenities = array();
                        if (!empty($amenities))
                            foreach ($amenities as $key => $amenitie) {
                                $_amenities[] = get_term_by('slug', $amenitie, 'amenities');
                            }


                        $item['profile_amenities'] = empty($user->profile_amenities) ? array() : $_amenities;
                        $item['audio_video_urls'] =empty($user->audio_video_urls) ? array() : $user->audio_video_urls;
                        $item['qualification'] = empty($user->qualification) ? array() : $user->qualification;
                        $item['privacy_settings'] = empty($user->privacy_settings) ? new stdClass() : $user->privacy_settings;
                        $item['profile_photo'] = $user->profile_photo;
                        $item['profile_banner'] = $user->profile_banner;
                        $item['profile_appointment'] = $user->profile_appointment;
                        $item['profile_contact'] = $user->profile_contact;
                        $item['profile_hours'] = $user->profile_hours;
                        $item['profile_service'] = $user->profile_service;
                        $item['profile_team'] = $user->profile_team;
                        $item['profile_gallery'] = $user->profile_gallery;
                        $item['profile_videos'] = $user->profile_videos;
                        $item['appt_booking_approved'] = $user->appt_booking_approved;
                        $item['appt_booking_confirmed'] = $user->appt_booking_confirmed;
                        $item['appt_booking_cancelled'] = $user->appt_booking_cancelled;
                        $item['appt_cancelled_title'] = $user->appt_cancelled_title;
                        $item['appt_approved_title'] = $user->appt_approved_title;
                        $item['appt_confirmation_title'] = $user->appt_confirmation_title;
                        $item['appointment_currency'] = $user->appointment_currency;
                        $item['appointment_inst_desc'] = $user->appointment_inst_desc;
                        $item['appointment_inst_title'] = $user->appointment_inst_title;


                        $item['profile_services'] = empty($user->profile_services) ? array() : array_values($user->profile_services);
                        $item['appointment_reasons'] = empty($user->appointment_reasons) ? array() : array_values($user->appointment_reasons);
                        $item['appointment_types'] = empty($user->appointment_types) ? array() : array_values($user->appointment_types);
                        $item['teams_data'] = empty($user->teams_data) ? array() : (array) array_values($user->teams_data);

                        $review_data = get_user_meta($user->ID, 'review_data', true);
                        $item['review_data'] = empty($review_data) ? new stdClass() : $review_data;

                        $items[] = $item;
                    }
                }

                // print_r($items);die;
                return new WP_REST_Response($items, 200);



                $items = array();
                if( !empty($services )){
                    $items = ($services);    
                }
                
                return new WP_REST_Response($items, 200);
            }





        public function listingo_image_uploader($user_id, $media_object, $image_upload_type='profile_avatar',  $type = 'profile_photo', $size_type = 'thumbnail') {

//var_dump($user_id, $media_object);die;


                $user_identity = $user_id;
                $attach_id = $media_object['id'];
                
                if (!empty($type) && $type === 'profile_photo') {
                    $size_type = 'avatar';
                } elseif (!empty($type) && $type === 'profile_banner_photo') {
                    $size_type = 'banner';
                } elseif (!empty($type) && $type === 'profile_award') {
                    $size_type = 'award';
                }

                $attachment_json =  $media_object['media_details']['sizes'];


                $is_replace = 'no';

                    $profile_meta = get_user_meta($user_identity, $image_upload_type, true);

                    $data_array = array();
                    $profile_meta['default_image'] = $attach_id;
                    if (!empty($profile_meta['image_data'])) {
                        $attach_array[$attach_id] = array(
                            'full' => $attachment_json['full']['source_url'],
                            'thumb' => empty($attachment_json['thumbnail']['source_url'] ) ? $attachment_json['full']['source_url']: $attachment_json['thumbnail']['source_url'],
                            'banner' => empty($attachment_json['listingo_user_banner_profile']['source_url'] ) ? $attachment_json['full']['source_url']: $attachment_json['listingo_user_banner_profile']['source_url'] ,
                            'image_id' => $attach_id
                        );
                        $is_replace = 'no';
                        $profile_meta['image_data'] = $profile_meta['image_data'] + $attach_array;
                        update_user_meta($user_identity, $image_upload_type, $profile_meta);
                    } else {


                        $data_array = array(
                            'image_type' => $type,
                            'default_image' => $attach_id,
                            'image_data' => array(
                                $attach_id => array(
                                    'full' => $attachment_json['full']['source_url'],
                                    'thumb' =>empty($attachment_json['thumbnail']['source_url'] ) ? $attachment_json['full']['source_url']: $attachment_json['thumbnail']['source_url'],
                                    'banner' => empty($attachment_json['listingo_user_banner_profile']['source_url'] ) ? $attachment_json['full']['source_url']: $attachment_json['listingo_user_banner_profile']['source_url'],
                                    'image_id' => $attach_id
                                ),
                            )
                        );

                        $is_replace = 'yes';
                        update_user_meta($user_identity, $image_upload_type, $data_array);
                    }
                    
                    update_user_meta($user_identity, 'is_avatar_available', 1);
                    

    }

    public function set_profile_date($request) {
                
        $params = $request->get_params();
  //      var_dump($params);
//die('adfss');
            
        $ID = $params['ID'];


        if(!empty($params['nickname'])){
            update_user_meta( $ID, 'nickname',  $params['nickname']);
        }
        if(!empty($params['first_name'])){
            update_user_meta( $ID, 'first_name',  $params['first_name']);
        }
        if(!empty($params['last_name'])){
            update_user_meta( $ID, 'last_name',  $params['last_name']);
        }
        if(!empty($params['description'])){
            update_user_meta( $ID, 'description',  $params['description']);
        }
        if(!empty($params['comment_shortcuts'])){
            update_user_meta( $ID, 'comment_shortcuts',  $params['comment_shortcuts']);
        }
        if(!empty($params['locale'])){
            update_user_meta( $ID, 'locale',  $params['locale']);
        }
        if(!empty($params['verify_user'])){
            update_user_meta( $ID, 'verify_user',  $params['verify_user']);
        }
        if(!empty($params['full_name'])){
            update_user_meta( $ID, 'full_name',  $params['full_name']);
        }
        if(!empty($params['activation_status'])){
            update_user_meta( $ID, 'activation_status',  $params['activation_status']);
        }
        if(!empty($params['profile_photo'])){
            update_user_meta( $ID, 'profile_photo',  $params['profile_photo']);
        }
        if(!empty($params['profile_banner'])){
            update_user_meta( $ID, 'profile_banner',  $params['profile_banner']);
        }
        if(!empty($params['profile_appointment'])){
            update_user_meta( $ID, 'profile_appointment',  $params['profile_appointment']);
        }
        if(!empty($params['profile_contact'])){
            update_user_meta( $ID, 'profile_contact',  $params['profile_contact']);
        }
        if(!empty($params['profile_hours'])){
            update_user_meta( $ID, 'profile_hours',  $params['profile_hours']);
        }
        if(!empty($params['profile_service'])){
            update_user_meta( $ID, 'profile_service',  $params['profile_service']);
        }
        if(!empty($params['profile_team'])){
            update_user_meta( $ID, 'profile_team',  $params['profile_team']);
        }
        if(!empty($params['profile_gallery'])){
            update_user_meta( $ID, 'profile_gallery',  $params['profile_gallery']);
        }
        if(!empty($params['profile_videos'])){
            update_user_meta( $ID, 'profile_videos',  $params['profile_videos']);
        }
        if(!empty($params['company_name'])){
            update_user_meta( $ID, 'company_name',  $params['company_name']);
        }
        
        if(!empty($params['address'])){
            update_user_meta( $ID, 'address',  $params['address']);
        }
        if(!empty($params['zip'])){
            update_user_meta( $ID, 'zip',  $params['zip']);
        }
        if(!empty($params['tag_line'])){
            update_user_meta( $ID, 'tag_line',  $params['tag_line']);
        }
        if(!empty($params['phone'])){
            update_user_meta( $ID, 'phone',  $params['phone']);
        }
        if(!empty($params['city'])){
            update_user_meta( $ID, 'city',  $params['city']);
        }
        if(!empty($params['country'])){
            update_user_meta( $ID, 'country',  $params['country']);
        }
        if(!empty($params['fax'])){
            update_user_meta( $ID, 'fax',  $params['fax']);
        }
        if(!empty($params['facebook'])){

            update_user_meta( $ID, 'facebook',  $params['facebook']);
        }
        if(!empty($params['twitter'])){
            update_user_meta( $ID, 'twitter',  $params['twitter']);
        }
        if(!empty($params['linkedin'])){
            update_user_meta( $ID, 'linkedin',  $params['linkedin']);
        }
        if(!empty($params['pinterest'])){
            update_user_meta( $ID, 'pinterest',  $params['pinterest']);
        }
        if(!empty($params['google_plus'])){
            update_user_meta( $ID, 'google_plus',  $params['google_plus']);
        }
        if(!empty($params['instagram'])){
            update_user_meta( $ID, 'instagram',  $params['instagram']);
        }
        if(!empty($params['tumblr'])){
            update_user_meta( $ID, 'tumblr',  $params['tumblr']);
        }
        if(!empty($params['skype'])){
            update_user_meta( $ID, 'skype',  $params['skype']);
        }
        if(!empty($params['last_update'])){
            update_user_meta( $ID, 'last_update',  $params['last_update']);
        }
        if(!empty($params['phone_number'])){
            update_user_meta( $ID, 'phone_number',  $params['phone_number']);
        }
        if(!empty($params['email'])){
            update_user_meta( $ID, 'email',  $params['email']);
        }
        if(!empty($params['category'])){
            //update_user_meta( $ID, 'category',  $params['category']);
        }
        if(!empty($params['sub_category'])){
            update_user_meta( $ID, 'sub_category',  serialize( $params['sub_category']) );
        }
        if(!empty($params['latitude'])){
            update_user_meta( $ID, 'latitude',  $params['latitude']);
        }
        if(!empty($params['longitude'])){
            update_user_meta( $ID, 'longitude',  $params['longitude']);
        }
        
        if(!empty($params['professional_statements'])){
            update_user_meta( $ID, 'professional_statements',  $params['professional_statements']);
        }
        
        if(!empty($params['sub_category'])){
            update_user_meta( $ID, 'sub_category',  $params['sub_category']);
        }


        
        
        //&& !isset($params['avatarObject']['isSelected'])
        if(!empty($params['avatarObject']) && count( $params['avatarObject'] ) && $params['avatarObject']['isSelected']  ){
        
            $this->listingo_image_uploader($ID, $params['avatarObject'], 'profile_avatar',  'profile_photo');
        }
        
        //&& !isset( $params['bannerObject']['isSelected']) 
        if(!empty($params['bannerObject']) && count( $params['bannerObject'] ) && $params['avatarObject']['isSelected']  ){
            $this->listingo_image_uploader($ID, $params['bannerObject'] , 'profile_banner_photos',  'profile_banner_photo');
        }
        


      
    //  var_dump($params);
      // if(!empty($params['banner'])){
      //       update_user_meta( $ID, 'profile_banner_photos',  $params['banner']);
      //   }
      //   if(!empty($params['avatar'])){
      //       update_user_meta( $ID, 'profile_avatar',  $params['avatar']);
      //   }
      
      

        $items['type'] =  "success";
                $items['message'] =  'Saved';
                return new WP_REST_Response($items, 200);

        
    }
    public function listingo_get_reviews_evaluation($category_type, $reviews_type) {

            $reviews_evaluation = array('leave_rating' => array(),'total_wait_time' =>array() );

            $reviews = '';
            if (function_exists('fw_get_db_settings_option')) {
                $reviews = fw_get_db_post_option($category_type, 'enable_reviews', true);
            }   
            // echo $reviews_type;
            // print_r($reviews);
            // print_r($reviews['enable'][$reviews_type]);
            // die;
            if( !empty( $reviews['enable'][$reviews_type] )){
                    foreach ($reviews['enable'][$reviews_type] as $key => $value) {
                        $reviews_evaluation['leave_rating'][$key]['title'] = $value;
                        $reviews_evaluation['leave_rating'][$key]['slug'] = sanitize_title($value);
                    }
                     foreach ($reviews['enable']['total_wait_time'] as $key => $value) {
                        $reviews_evaluation['total_wait_time'][] = $value;
                    }
            }
            

            // $reviews_check = !empty($reviews['gadget']) ? $reviews['gadget'] : '';

            // if (!empty($reviews_check) && $reviews_check === 'enable' && $reviews_type === 'total_wait_time') {
            //     $reviews_evaluation = !empty($reviews['enable'][$reviews_type]) ? $reviews['enable'][$reviews_type] : array();
            // } else if (!empty($reviews_check) && $reviews_check === 'enable' && $reviews_type === 'leave_rating') {
            //     $reviews_evaluation = !empty($reviews['enable'][$reviews_type]) ? $reviews['enable'][$reviews_type] : array();
            // }

            // $reviews_evaluation = array_filter($reviews_evaluation);
            // $reviews_evaluation = array_combine(array_map('sanitize_title', $reviews_evaluation), $reviews_evaluation);
            return $reviews_evaluation;
        }
            public function set_my_services($request) {
                $params = $request->get_params();

             //   print_r($params['profile_services']);die;
                $services  =  update_user_meta($params['user_id'],'profile_services', ($params['profile_services']));
                $items['type'] =  "success";
                $items['message'] =  'Saved';
                return new WP_REST_Response($items, 200);
            }

            public function set_my_favorites($request) {
                $params = $request->get_params();
                $favorites  =  get_user_meta($params['user_id'], 'wishlist', true);
                
                if( $params['isfav'] == 'yes'){
                     if( empty( $favorites )){
                    $favorites= array();
                    }
                    array_push($favorites, $params['provider_id']);
                }

                if( $params['isfav'] == 'no'){
                   // print_r($favorites);
                    if( !empty( $favorites )){
                        //unset($favorites[  ]);
                       $favorites = array_diff($favorites, array($params['provider_id']));
                    }
                }
               // print_r($favorites);die;
               
                $services  =  update_user_meta($params['user_id'], 'wishlist', $favorites );
                $items['type'] =  "success";
                $items['message'] =  'Saved';
                return new WP_REST_Response($items, 200);
            }

            public function get_my_appoinments($request) {

                $sort_by = !empty($_GET['sortby']) ? $_GET['sortby'] : 'ID';
                $showposts = !empty($_GET['showposts']) ? $_GET['showposts'] : -1;
                $items = array();
                //Order
                $order = 'DESC';
                if (!empty($_GET['orderby'])) {
                    $order = esc_attr($_GET['orderby']);
                }

                if (!empty($_GET['appointment_date'])) {
                    $apt_date = strtotime(esc_attr($_GET['appointment_date']));
                }

                $status = array('pending', 'publish');
                if (!empty($_GET['appointment_status'])) {
                    $status = array();
                    $status[] = $_GET['appointment_status'];
                }


                $query_args = array(
                    'posts_per_page' => "-1",
                    'post_type' => 'sp_appointments',
                    'order' => $order,
                    'orderby' => $sort_by,
                    'post_status' => $status,
                    'ignore_sticky_posts' => 1);

                $total_query = new WP_Query($query_args);
                $total_posts = $total_query->post_count;

                $query_args = array(
                    'posts_per_page' => $showposts,
                    'post_type' => 'sp_appointments',
                    'paged' => $paged,
                    'order' => $order,
                    'orderby' => $sort_by,
                    'post_status' => $status,
                    'ignore_sticky_posts' => 1);

                $meta_query_args[] = array(
                    'key' => 'apt_user_from',
                    'value' => $_GET['user_id'],
                    'compare' => '=',
                    'type' => 'NUMERIC'
                );

                if (!empty($apt_date)) {
                    $meta_query_args[] = array(
                        'key' => 'apt_date',
                        'value' => esc_attr($apt_date),
                        'compare' => '=',
                    );
                }
                if (!empty($meta_query_args)) {
                    $query_relation = array('relation' => 'AND',);
                    $meta_query_args = array_merge($query_relation, $meta_query_args);
                    $query_args['meta_query'] = $meta_query_args;
                }

                $items = array();

                $appt_data = new WP_Query($query_args);
                $date_format = get_option('date_format');
                $time_format = get_option('time_format');
                if ($appt_data->have_posts()) {
                    $counter = 1;
                    while ($appt_data->have_posts()) : $appt_data->the_post();
                        global $post;
                        $item['apt_types'] = $apt_types = get_post_meta($post->ID,
                                'apt_types', true);

                        $item['key'] = $apt_types = get_the_title();
                        $item['apt_services'] = $apt_services = get_post_meta($post->ID,
                                'apt_services', true);
                        $item['apt_reasons'] = $apt_reasons = get_post_meta($post->ID,
                                'apt_reasons', true);
                        $item['apt_description'] = $apt_description = get_post_meta($post->ID,
                                'apt_description', true);
                        $apt_user_from = get_post_meta($post->ID, 'apt_user_from',
                                true);
                        $apt_user_to = get_post_meta($post->ID, 'apt_user_to', true);



                        $item['apt_currency_symbol'] = $apt_currency_symbol = get_post_meta($post->ID,
                                'apt_currency_symbol', true);
                        $item['apt_user_to'] = $apt_user_to = get_post_meta($post->ID,
                                'apt_user_to', true);
                        $item['provider'] = $username = listingo_get_username($apt_user_to);
                        $item['apt_date'] = $apt_date = get_post_meta($post->ID,
                                'apt_date', true);
                        $apt_time = get_post_meta($post->ID,
                                'apt_time', true);
    $time = explode('-', $apt_time);
     $item['apt_time'] = date_i18n($time_format, strtotime('2016-01-01 ' . $time[0])) . ' - ' .date_i18n($time_format, strtotime('2016-01-01 ' . $time[1]));
                        $item['time'] = $time = explode('-', $apt_time);

                        $item['booking_services'] = $booking_services = get_user_meta($apt_user_to,
                                'profile_services', true);
                        $item['booking_types'] = $booking_types = get_user_meta($apt_user_to,
                                'appointment_types', true);
                        $item['booking_reasons'] = $booking_reasons = get_user_meta($apt_user_to,
                                'appointment_reasons', true);

                        $item['apt_user_from'] = $apt_user_from = get_post_meta($post->ID,
                                'apt_user_from', true);
                        $item['username'] = $username = listingo_get_username($apt_user_from);

                        $item['avatar'] = $avatar = apply_filters(
                                'listingo_get_media_filter',
                                listingo_get_user_avatar(array('width' => 100, 'height' => 100),
                                        $apt_user_from),
                                array('width' => 100, 'height' => 100)
                        );
                        $items[] = $item;
                        $counter++;
                    endwhile;
                    wp_reset_postdata();
                }


                return new WP_REST_Response($items, 200);
            }

            
            /**
             * Login user for application
             *
             * @param WP_REST_Request $request Full data about the request.
             * @return WP_Error|WP_REST_Request
             */
            public function save_user_device_token($request) {
                 $params = $request->get_params();
                 
                    $user = get_userdata( $params['user_id']);

                if ( $user === false ) {
                    $json['message'] = 'User does not exists!';
                } else {

                     $tokens = get_user_meta($params['user_id'], 'device_token' );
                    if ( !in_array($params['device_token'], $tokens )  ){ 
                         add_user_meta( $params['user_id'] , 'device_token', $params['device_token']);
                        $json['message'] = 'Token saved!';
                    }else{
                        $json['message'] = 'Token already exists';
                    }
                    
                    
                }

                return new WP_REST_Response($json, 200);

            } 

            public function remove_user_device_token($request) {
                $params = $request->get_params();
                 
                $user = get_userdata( $params['user_id']);

                if ( $user === false ) {
                    $json['message'] = 'User does not exists!';
                } else {

                     $tokens = get_user_meta($params['user_id'], 'device_token' );
                    if ( in_array($params['device_token'], $tokens )  ){ 
                         delete_user_meta( $params['user_id'] , 'device_token', $params['device_token']);
                        $json['message'] = 'Token removed!';
                    }else{
                        $json['message'] = 'Token does not exists';
                    }
                    
                    
                }

                return new WP_REST_Response($json, 200);

            } 


            /**
             * Login user for application
             *
             * @param WP_REST_Request $request Full data about the request.
             * @return WP_Error|WP_REST_Request
             */
            public function user_login($request) {
                $params = $request->get_params();
                $_POST = $request->get_params();

                if(isset($params['googleLogin']) && isset($params['username'])
                        && ((boolean)$params['googleLogin'])){
                     
                     $user = get_user_by("email", $params['username']);

                    unset($user->allcaps);
                    unset($user->filter);
                    $user->meta = get_user_meta($user->data->ID, '', true);

                    $user->avatar = apply_filters(
                            'listingo_get_media_filter',
                            listingo_get_user_avatar(array('width' => 100, 'height' => 100),
                                    $user->data->ID),
                            array('width' => 100, 'height' => 100)
                    );

                    $user->banner = apply_filters(
                            'listingo_get_media_filter',
                            listingo_get_user_banner(array('width' => 270, 'height' => 120),
                                    $user->data->ID),
                            array('width' => 270, 'height' => 120)//size width,height
                    );

                    if (is_wp_error($user)) {
                        return new WP_Error('wrong-credentials',
                                __('message', 'listingo-app'), array('status' => 500));
                    } else {
                        $json['type'] = "success";
                        $json['message'] = esc_html__(".", "listingo_core");
                        $json['data'] = $user;
                        return new WP_REST_Response($json, 200);
                    }
                }else if (isset($params['username']) && isset($params['password'])) {
                    $creds = array(
                        'user_login' => $params['username'],
                        'user_password' => $params['password'],
                        'remember' => true
                    );

                    $user = wp_signon($creds, false);
                    unset($user->allcaps);
                    unset($user->filter);
                    $user->meta = get_user_meta($user->data->ID, '', true);

                    $user->avatar = apply_filters(
                            'listingo_get_media_filter',
                            listingo_get_user_avatar(array('width' => 100, 'height' => 100),
                                    $user->data->ID),
                            array('width' => 100, 'height' => 100)
                    );

                    $user->banner = apply_filters(
                            'listingo_get_media_filter',
                            listingo_get_user_banner(array('width' => 270, 'height' => 120),
                                    $user->data->ID),
                            array('width' => 270, 'height' => 120)//size width,height
                    );

                    if (is_wp_error($user)) {
                        return new WP_Error('wrong-credentials',
                                __('message', 'listingo-app'), array('status' => 500));
                    } else {
                        $json['type'] = "success";
                        $json['message'] = esc_html__(".", "listingo_core");
                        $json['data'] = $user;
                        return new WP_REST_Response($json, 200);
                    }
                }




                return new WP_Error('cant-create', __('message', 'listingo-app'),
                        array('status' => 500));
            }

            /**
             * Forgot password for application
             *
             * @param WP_REST_Request $request Full data about the request.
             * @return WP_Error|WP_REST_Request
             */
            public function forgot_password($request) {
                $params = $request->get_params();
                $_POST = $request->get_params();
                $json = array();
                if (isset($params['email'])) {
                    $user_login = $params['email'];
                    $status = true;
                    $response_message = '';
                    $json['message'] = 'Some error occured';
                    global $wpdb, $wp_hasher;

                    $user_login = sanitize_text_field($user_login);


                    if (empty($user_login)) {
                        $status = false;
                        $response_message = 'Please enter email address';
                    } else if (strpos($user_login, '@')) {
                        $user_data = get_user_by('email', trim($user_login));
                        if (empty($user_data))
                            $status = false;
                        $response_message = 'Email address does not exist';
                    } else {
                        $login = trim($user_login);
                        $user_data = get_user_by('login', $login);
                    }

                    do_action('lostpassword_post');


                    if ($user_data) {



                        // redefining user_login ensures we return the right case in the email
                        $user_login = $user_data->user_login;
                        $user_email = $user_data->user_email;

                        do_action('retreive_password', $user_login);  // Misspelled and deprecated
                        do_action('retrieve_password', $user_login);

                        $allow = apply_filters('allow_password_reset', true,
                                $user_data->ID);

                        if (!$allow) {
                            $status = false;
                            $json['message'] = 'Password change not allowed';
                        } else if (is_wp_error($allow))
                            $status = false;

                        $key = wp_generate_password(20, false);
                        do_action('retrieve_password_key', $user_login, $key);

                        if (empty($wp_hasher)) {
                            require_once ABSPATH . 'wp-includes/class-phpass.php';
                            $wp_hasher = new PasswordHash(8, true);
                        }
                        $hashed = $wp_hasher->HashPassword($key);
                        $wpdb->update($wpdb->users,
                                array('user_activation_key' => $hashed),
                                array('user_login' => $user_login));

                        $message = __('Someone requested that the password be reset for the following account:') . "\r\n\r\n";
                        $message .= network_home_url('/') . "\r\n\r\n";
                        $message .= sprintf(__('Username: %s'), $user_login) . "\r\n\r\n";
                        $message .= __('If this was a mistake, just ignore this email and nothing will happen.') . "\r\n\r\n";
                        $message .= __('To reset your password, visit the following address:') . "\r\n\r\n";
                        $message .= '<' . network_site_url("wp-login.php?action=rp&key=$key&login=" . rawurlencode($user_login),
                                        'login') . ">\r\n";

                        if (is_multisite())
                            $blogname = $GLOBALS['current_site']->site_name;
                        else
                            $blogname = wp_specialchars_decode(get_option('blogname'),
                                    ENT_QUOTES);

                        $title = sprintf(__('[%s] Password Reset'), $blogname);

                        $title = apply_filters('retrieve_password_title', $title);
                        $message = apply_filters('retrieve_password_message',
                                $message, $key);

                        if ($message && !wp_mail($user_email, $title, $message))
                            $response_message = ( __('The e-mail could not be sent.') . "<br />\n" . __('Possible reason: your host may have disabled the mail() function...') );

                        $response_message = '<p>Link for password reset has been emailed to you. Please check your email.</p>';
                    }

                    $json['message'] = $response_message;
                    if ($status) {
                        $json['type'] = "success";
                        $json['data'] = array();
                        return new WP_REST_Response($json, 200);
                    } else {
                        $json['type'] = "error";
                        return new WP_REST_Response($json, 200);
                    }
                }
            }

        }

    }
    add_action('rest_api_init',
            function () {
        $controller = new ListingoApp_User_Route;
        $controller->register_routes();
    });
