'use strict';

angular.module('cloudoptingApp')
    .controller('ContactController', function(SERVICE, $location, $translate, $scope, $log, $state, localStorageService, Contact) {

        $scope.success = null;
        $scope.error = null;
        $scope.message = null;

        $scope.contact = function () {
            $scope.error = null;

            var callback = function (data){
                if(data) {
                    $scope.success = true;
                } else {
                    $scope.error = true;
                }
            };

            Contact.save($scope.message, callback);
        };

    }
);