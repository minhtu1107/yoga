<?php

if (!class_exists('ListingoApp_Jobs_Route')) {

    class ListingoApp_Jobs_Route extends WP_REST_Controller
    {

        /**
         * Register the routes for the objects of the controller.
         */
        public function register_routes() {
            $version = '1';
            $namespace = 'api/v' . $version;
            $base = 'jobs';

            register_rest_route($namespace, '/' . $base ,
                    array(
                array(
                    'methods' => WP_REST_Server::READABLE,
                    'callback' => array($this, 'get_jobs'),
                    // 'permission_callback' => array( $this, 'get_items_permissions_check' ),
                    'args' => array(
                    ),
                ),
            ));
        }

        /**
         * Get countries
         *
         * @param WP_REST_Request $request Full data about the request.
         * @return WP_Error|WP_REST_Response
         */
        public function get_jobs($request) {
            global $paged, $total_posts, $query_args, $showposts;
            $per_page = intval(10);
            if (!empty($_GET['showposts'])) {
                $per_page = $_GET['showposts'];
            }


            $pg_page = get_query_var('page') ? get_query_var('page') : 1; //rewrite the global var
            $pg_paged = get_query_var('paged') ? get_query_var('paged') : 1; //rewrite the global var
            //paged works on single pages, page - works on homepage
            $paged = max($pg_page, $pg_paged);

            $json = array();
            $meta_query_args    = array();

            if (!empty($_GET['category'])) {
                $category = listingo_get_page_by_slug($_GET['category'], 'sp_categories', 'id');
            } else {
                $category = '';
            }

            //search filters
            $sub_category   = !empty($_GET['sub_categories']) ? $_GET['sub_categories'] : '';
            $job_type       = !empty($_GET['job_type']) ? $_GET['job_type'] : '';
            $experience     = !empty($_GET['experience']) ? $_GET['experience'] : '';
            $languages      = !empty($_GET['languages']) ? $_GET['languages'] : '';
            $sort_by        = !empty($_GET['sortby']) ? $_GET['sortby'] : 'ID';
            $showposts      = !empty($_GET['showposts']) ? $_GET['showposts'] : -1;

            //Order
            $order = 'DESC';
            if (!empty($_GET['orderby'])) {
                $order = esc_attr($_GET['orderby']);
            }

            //Category Type Search
            if (!empty($category)) {
                $meta_query_args[] = array(
                    'key'       => 'category',
                    'value'     => $category,
                    'compare'   => '=',
                );
            }
            //Sub category Type Search
            if (!empty($sub_category) && !empty($sub_category[0]) && is_array($sub_category)) {
                $query_relation = array('relation' => 'OR',);
                $sub_category_args = array();
                foreach ($sub_category as $key => $value) {
                    $sub_category_args[] = array(
                        'key' => 'sub_category',
                        'value' => $value,
                        'compare' => '='
                    );
                }

                $meta_query_args[] = array_merge($query_relation, $sub_category_args);
            }

            //Job Type Search
            if (!empty($job_type) && !empty($job_type[0]) && is_array($job_type)) {
                $query_relation = array('relation' => 'OR',);
                $job_type_args = array();
                foreach ($job_type as $key => $value) {
                    $job_type_args[] = array(
                        'key'       => 'job_type',
                        'value'     => $value,
                        'compare'   => '='
                    );
                }

                $meta_query_args[] = array_merge($query_relation, $job_type_args);
            }

            //Experience Type Search
            if (!empty($experience) && !empty($experience[0]) && is_array($experience)) {
                $query_relation = array('relation' => 'OR',);
                $experience_args = array();
                foreach ($experience as $key => $value) {
                    $experience_args[] = array(
                        'key' => 'experience',
                        'value' => $value,
                        'compare' => '='
                    );
                }

                $meta_query_args[] = array_merge($query_relation, $experience_args);
            }

            //Experience Type Search
            if (!empty($experience) && !empty($experience[0]) && is_array($experience)) {
                $query_relation = array('relation' => 'OR',);
                $experience_args = array();
                foreach ($experience as $key => $value) {
                    $experience_args[] = array(
                        'key' => 'experience',
                        'value' => $value,
                        'compare' => '='
                    );
                }

                $meta_query_args[] = array_merge($query_relation, $experience_args);
            }


            //Language Search;
            if (!empty($languages) && !empty($languages[0]) && is_array($languages)) {
                $query_relation = array('relation' => 'OR',);
                $language_args = array();
                foreach ($languages as $key => $value) {
                    $language_args[] = array(
                        'key' => 'languages',
                        'value' => serialize(strval($value)),
                        'compare' => 'LIKE'
                    );
                }

                $meta_query_args[] = array_merge($query_relation, $language_args);
            }

            $query_args = array(
                'posts_per_page' => "-1",
                'post_type' => 'sp_jobs',
                'order' => $order,
                'orderby' => $sort_by,
                'post_status' => 'publish',
                'ignore_sticky_posts' => 1);

            if (!empty($_GET['keyword'])) {
                $s = sanitize_text_field($_GET['keyword']);
                $query_args['s'] = $s;
            }

            $total_query = new WP_Query($query_args);
            $total_posts = $total_query->post_count;

            $query_args = array(
                'posts_per_page' => $showposts,
                'post_type' => 'sp_jobs',
                'paged' => $paged,
                'order' => $order,
                'orderby' => $sort_by,
                'post_status' => 'publish',
                'ignore_sticky_posts' => 1);


            if (!empty($meta_query_args)) {
                $query_relation = array('relation' => 'AND',);
                $meta_query_args = array_merge($query_relation, $meta_query_args);
                $query_args['meta_query'] = $meta_query_args;
            }
            if (!empty($_GET['keyword'])) {
                $s = sanitize_text_field($_GET['keyword']);
                $query_args['s'] = $s;
            }
            $job_data = new WP_Query($query_args);


            $jobs = array();


            if ($job_data->have_posts()) {
                while ($job_data->have_posts()) : $job_data->the_post();
                    $job = array();
                    global $post;
                    $job_type = '';
                    $career_level = '';
                    $location = '';
                    if (function_exists('fw_get_db_post_option')) {
                        $job_type = fw_get_db_post_option($post->ID, 'job_type', true);
                        $career_level = fw_get_db_post_option($post->ID, 'career_level', true);
                        $location = fw_get_db_post_option($post->ID, 'address', true);
                        $list_career_type = listingo_get_career_level();
                        $list_job_types = listingo_get_job_type();
                        if (array_key_exists($job_type, $list_job_types)) {
                            $job_type = $list_job_types[$job_type];
                        }
                        if (array_key_exists($career_level, $list_career_type)) {
                            $career_level = $list_career_type[$career_level];
                        }
                    }

                    $post_author = '';
                    $career_level = '';
                    $job_type = '';
                    $experience = '';
                    $salary = '';
                    $qualification = '';
                    $languages = '';
                    $requirements = '';
                    $benifits = '';
                    $expiry_date = '';
                    if (function_exists('fw_get_db_post_option')) {
                        $career_level = fw_get_db_post_option($post->ID, 'career_level', true);
                        $list_career_levels = listingo_get_career_level();
                        if (array_key_exists($career_level, $list_career_levels)) {
                            $career_level = $list_career_levels[$career_level];
                        }
                        
                        $job_type = fw_get_db_post_option($post->ID, 'job_type', true);
                        $list_job_types = listingo_get_job_type();
                        
                        if (array_key_exists($job_type, $list_job_types)) {
                            $job_type = $list_job_types[$job_type];
                        }
                        
                        $experience = fw_get_db_post_option($post->ID, 'experience', true);
                        $salary = fw_get_db_post_option($post->ID, 'salary', true);
                        $qualification = fw_get_db_post_option($post->ID, 'qualification', true);
                        $languages = fw_get_db_post_option($post->ID, 'languages', true);
                        $requirements = fw_get_db_post_option($post->ID, 'requirements', true);
                        $benefits = fw_get_db_post_option($post->ID, 'benifits', true);
                        $expiry_date = fw_get_db_post_option($post->ID, 'expirydate', true);
                        $post_author = get_post_meta($post->ID, 'author', true);
                        
                        //Get Location Detail.
                        $profile_address = fw_get_db_post_option($post->ID, 'address', true);
                        $latitude = fw_get_db_post_option($post->ID, 'address_latitude', true);
                        $longitude = fw_get_db_post_option($post->ID, 'address_longitude', true);
                        $phone = fw_get_db_post_option($post->ID, 'phone', true);
                        $fax = fw_get_db_post_option($post->ID, 'fax', true);
                        $email = fw_get_db_post_option($post->ID, 'email', true);
                        $user_url = fw_get_db_post_option($post->ID, 'url', true);
                    }

                    $avatar = apply_filters(
                            'listingo_get_media_filter', listingo_get_user_avatar(array('width' => 100, 'height' => 100), $post_author), array('width' => 100, 'height' => 100)
                    );
                   
                    $author_name = listingo_get_username($post_author);
                    
                    //Get the total job views.
                    $job_views = apply_filters('sp_get_profile_views', $post->ID, 'set_job_view');
                    $job_total_views = esc_html__('Total Views: ', 'listingo') . intval(0);
                    if (!empty($job_views)) {
                        $job_total_views = esc_html__('Total Views: ', 'listingo') . intval($job_views);
                    }

                    //Get user meta social links detail.
                    $facebook = get_user_meta($post_author, 'facebook', true);
                    $twitter = get_user_meta($post_author, 'twitter', true);
                    $linkedin = get_user_meta($post_author, 'linkedin', true);
                    $pinterest = get_user_meta($post_author, 'pinterest', true);
                    $googleplus = get_user_meta($post_author, 'googleplus', true);
                    $skype = get_user_meta($post_author, 'skype', true);



                    $job['avatar'] = apply_filters(
                            'listingo_get_media_filter', listingo_get_user_avatar(array('width' => 100, 'height' => 100), $post->post_author), array('width' => 100, 'height' => 100)
                    );
                    $job['author_name'] = listingo_get_username($post->post_author);
                    $job['job_type'] = $job_type;
                    $job['link'] = get_permalink();
                    $job['title'] = get_the_title();
                    $job['location'] = esc_attr($location);
                    $job['career_level'] = esc_attr($career_level);
                    $job['time'] = human_time_diff(strtotime(get_the_date()), current_time('timestamp'));
                    $job['description'] = get_the_content();

                    $job['experience'] = $experience ;
                    $job['salary'] = $salary ;
                    $job['qualification'] = $qualification ;
                    $job['languages'] = $languages ;
                    $job['requirements'] = $requirements ;
                    $job['benefits'] = $benefits ;
                    $job['expiry_date'] = $expiry_date ;
                    $job['post_author'] = $post_author ;
//Get Location Detail. ;
                    $job['profile_address'] = $profile_address ;
                    $job['latitude'] = $latitude ;
                    $job['longitude'] = $longitude ;
                    $job['phone'] = $phone ;
                    $job['fax'] = $fax ;
                    $job['email'] = $email ;
                    $job['user_url'] = $user_url ;




                    $jobs[] = $job;
                endwhile;
                
            }else{
                // Listingo_Prepare_Notification::listingo_info('Info', 'Sorry we could not find any job right now.');
            }




            return new WP_REST_Response($jobs, 200);
        }

    }

}
add_action('rest_api_init',
        function () {
    $controller = new ListingoApp_Jobs_Route;
    $controller->register_routes();
});

