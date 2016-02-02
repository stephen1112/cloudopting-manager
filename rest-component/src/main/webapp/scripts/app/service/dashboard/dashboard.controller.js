'use strict';

angular.module('cloudoptingApp')
    .controller('DashboardController', function (SERVICE, $scope, $state, $log, $location, Principal, localStorageService, InstanceService, ProcessService, $window, Blob, FileSaver) {

        $scope.instancesList = null;

        $scope.isSubscriber = function(){
            if(Principal.isInRole(SERVICE.ROLE.ADMIN) || Principal.isInRole(SERVICE.ROLE.SUBSCRIBER)){
                return true;
            }
            return false;
        };

        $scope.isPublisher = function(){
            if(Principal.isInRole(SERVICE.ROLE.PUBLISHER)){
                return true;
            }
            return false;
        };

        $scope.toscaide = function(){
            $state.go('toscaide');
        }

        if(Principal.isInRole(SERVICE.ROLE.SUBSCRIBER) || Principal.isInRole(SERVICE.ROLE.ADMIN)) {
            //Get all instances of the user if it is a SUBSCRIBER.
            var callback = function(data, status, headers, config){
                $log.info(data);
            };

            InstanceService.findAll(callback)
                .success(function (instances) {
                    $scope.instancesList = instances;
                });
        }

        $scope.test = function(instance) {
            var callback = function (data, status, headers, config) {
                checkStatusCallback(data, status, headers, config, "Test requested.");
                if($scope.errorMessage==null) {
                    if(data) {
                        var zip = new Blob([data], {type: 'application/zip'});
                        var fileName = 'TOSCA_Archive_Test.zip';
                        FileSaver.saveAs(zip, fileName);
                    }
                }
            };
            ProcessService.test(instance, callback);
        };

        $scope.demo = function(instance) {
            var callback = function (data, status, headers, config) {
                checkStatusCallback(data, status, headers, config, "Demo requested.");
            };
            ProcessService.test(instance, callback);
        };

        $scope.deploy = function(instance) {
            var callback = function (data, status, headers, config) {
                checkStatusCallback(data, status, headers, config, "Deploy requested.");
            };
            ProcessService.deploy(instance, callback);
        };

        $scope.stop = function(instance) {
            $window.alert('Not implemented yet');
            var callback = function(data, status, headers, config){
                $log.info(data);
            };
            //InstanceService.stop(instance, callback);
        };
        $scope.delete = function(instance) {
            $window.alert('Not implemented yet');
            var callback = function(data, status, headers, config){
                $log.info(data);
            };
            //InstanceService.delete(instance, callback);
        };
        $scope.monitor = function(instance) {
            localStorageService.set(SERVICE.STORAGE.CURRENT_INSTANCE, instance);
            $state.go('monitoring');
        };
        $scope.start = function(instance) {
            $window.alert('Not implemented yet');
            var callback = function(data, status, headers, config){
                $log.info(data);
            };
            //InstanceService.start(instance, callback);
        };
        //Checks for showing the buttons.
        $scope.showDeploy = function(str){
            return str === "Requested";
        };
        $scope.showStop = function(str){
            return str === "Running";
        };
        $scope.showDelete = function(str){
            return str === "Running";
        };
        $scope.showStart = function(str){
            return str === "Stopped";
        };
        $scope.showMonitor = function(str){
            return str === "Running";
        };

        $scope.errorMessage = null;
        $scope.infoMessage = null;

        //function to check possible outputs for the end user information.
        var checkStatusCallback = function(data, status, headers, config, message){
            if(status==401) {
                //Unauthorised. Check if signed in.
                if(Principal.isAuthenticated()){
                    $scope.errorMessage = "You have no permissions to do so. Ask for more permissions to the administrator";
                } else {
                    $scope.errorMessage = "Your session has ended. Sign in again. Redirecting to login...";
                    $timeout(function() {
                        $state.go('login');
                    }, 3000);
                }
            }else if(status!=200 && status!=201) {
                //Show message
                $scope.errorMessage = "An error occurred. Wait a moment and try again, if problem persists contact the administrator";

            } else {
                //Return to the list
                $scope.infoMessage = message + " Successfully done!";
            }
        };
    }
);