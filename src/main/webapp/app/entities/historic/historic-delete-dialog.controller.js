(function() {
    'use strict';

    angular
        .module('jhipsterfourApp')
        .controller('HistoricDeleteController',HistoricDeleteController);

    HistoricDeleteController.$inject = ['$uibModalInstance', 'entity', 'Historic'];

    function HistoricDeleteController($uibModalInstance, entity, Historic) {
        var vm = this;

        vm.historic = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Historic.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
