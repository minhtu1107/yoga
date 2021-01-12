<?php

if (!class_exists('ListingoAppCategoryRoutes')) {

    class ListingoAppCategoryRoutes extends WP_REST_Controller
    {

        /**
         * Register the routes for the objects of the controller.
         */
        public function register_routes() {
            $version = '1';
            $namespace = 'api/v' . $version;
            $base = 'categories';

            register_rest_route($namespace, '/' . $base,
                    array(
                array(
                    'methods' => WP_REST_Server::READABLE,
                    'callback' => array($this, 'get_categories'),
                    'args' => array(
                    ),
                ),
            ));
        }

        /**
         * Get categories
         *
         * @param WP_REST_Request $request Full data about the request.
         * @return WP_Error|WP_REST_Response
         */
        public function get_categories($request) {

            $args = array('posts_per_page' => '-1',
                'post_type' => 'sp_categories',
                'post_status' => 'publish',
                'suppress_filters' => false
            );

            $options = '';
            $cust_query = get_posts($args);

            if (!empty($cust_query)) {
                $counter = 0;
                foreach ($cust_query as $key => $dir) {

                    $meta = get_post_meta($dir->ID);
                    $item = array();

                    $item['id'] = ($dir->ID);
                    $item['title'] = get_the_title($dir->ID);



                    $item += unserialize($meta['fw_options'][0]);

                    $item['category_image'] = empty($item['category_image']) ? new stdClass() : $item['category_image'];


                    if (isset($dir->ID)) {
                        $sub_categories = wp_get_post_terms($dir->ID,
                                'sub_category', array("fields" => "all"));

                        $subarray = array();
                        if (!empty($sub_categories)) {
                            foreach ($sub_categories as $key => $sub_category) {
                                if (!empty($sub_category)) {
                                    $sub['slug'] = $sub_category->slug;
                                    $sub['title'] = htmlspecialchars_decode($sub_category->name);
                                }
                                $subarray[] = $sub;
                            }
                        }

                        $item['sub_categories'] = $subarray;
                    }

                    $items[] = $item;
                }
            }


            return new WP_REST_Response($items, 200);
        }

    }

}

add_action('rest_api_init',
        function () {
    $controller = new ListingoAppCategoryRoutes;
    $controller->register_routes();
});
