(function() {
    'use strict';

    angular
        .module('jhipsterfourApp')
        .controller('HistoricDetailController', HistoricDetailController);

    HistoricDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Historic'];

    function HistoricDetailController($scope, $rootScope, $stateParams, previousState, entity, Historic) {
        var vm = this;

        vm.historic = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('jhipsterfourApp:historicUpdate', function(event, result) {
            vm.historic = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
