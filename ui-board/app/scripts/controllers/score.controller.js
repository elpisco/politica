'use strict';

boardModule.controller('scoreController', scoreController);
scoreController.$inject = ['$scope', 'scoreService', '$state', '$stateParams'];

function scoreController($scope, scoreService, $state, $stateParams) {

  function getCandidateScore(listOfCandidatesLists) {

    var colors = {
      RicardoAriasM: '#D66F13',
      MMMaldonadoC: '#FBD103',
      danielraisbeck: '#FF5C01',
      ClaraLopezObre: '#FFDF00',
      RafaelPardo: '#ED0A03',
      PachoSantosC: '#3C68B7',
      EnriquePenalosa: '#12ADE5',
      AlexVernot: '#0A5C6D',
      CVderoux: '#088543',
      FicoGutierrez: '#FE5859',
      AlcaldeAlonsoS: '#83AC2A',
      RICOGabriel: '#F6783B',
      jcvelezuribe: '#183A64',
      HectorHAlcalde: '#FFDF00'
    };

    var chart = c3.generate({
      bindto: '#scoreChart',
      size: {
        height: 530
      },
      data: {
        x: 'x',
        columns: listOfCandidatesLists,
        colors: colors,
        type: 'spline'
      },
      axis: {
        x: {
          label: {
            text: 'Fecha',
            position: 'outer-center'
          },
          type: 'timeseries',
          tick: {
            format: '%m-%d'
          }
        },
        y: {
          label: {
            text: 'Tweets',
            position: 'outer-middle'
          }
        }
      },
      legend: {
        position: 'right'
      }
    });

    return chart;
  }

  $scope.showAllCandidates = function() {
    $scope.chart.show(['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck']);
  };

  $scope.showPopularCandidatesOnly = function() {
    $scope.chart.hide(['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck']);
  };

  $scope.getTargetName = function(targetId) {

    var candidateNames = {
      RicardoAriasM: 'Ricardo Arias Mora',
      MMMaldonadoC: 'María Mercedes Maldonado',
      danielraisbeck: 'Daniel Raisbeck',
      ClaraLopezObre: 'Clara López Obregón',
      RafaelPardo: 'Rafael Pardo',
      PachoSantosC: 'Francisco Santos',
      EnriquePenalosa: 'Enrique Peñalosa',
      AlexVernot: 'Alex Vernot',
      CVderoux: 'Carlos Vicente de Roux',
      FicoGutierrez: 'Federico Gutiérrez',
      AlcaldeAlonsoS: 'Alonso Salazar',
      RICOGabriel: 'Gabriel Jaime Rico',
      jcvelezuribe: 'Juan Carlos Vélez',
      HectorHAlcalde: 'Héctor Hoyos'
    }

    return candidateNames[targetId];
  };

  function shuffleArray(o) {
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
  }

  var bogotaCandidates = {
    RicardoAriasM: '#D66F13',
    MMMaldonadoC: '#FBD103',
    danielraisbeck: '#FF5C01',
    ClaraLopezObre: '#FFDF00',
    RafaelPardo: '#ED0A03',
    PachoSantosC: '#3C68B7',
    EnriquePenalosa: '#12ADE5',
    AlexVernot: '#0A5C6D',
    CVderoux: '#088543'
  };

  var medellinCandidates = {
    FicoGutierrez: '#FE5859',
    AlcaldeAlonsoS: '#83AC2A',
    RICOGabriel: '#F6783B',
    jcvelezuribe: '#183A64',
    HectorHAlcalde: '#FFDF00'
  };

  function getCandidatesFromCity(cityId, candidatos) {

    var candidatesFromCity = [];

    for (var i = 0; i < candidatos.length; i++) {

      var candidateColor = null;
      var candidate = candidatos[i];

      if (cityId == 'bogota') {
        candidateColor = bogotaCandidates[candidate.target];
      } else {
        candidateColor = medellinCandidates[candidate.target];
      }

      if (candidateColor) {
        candidatesFromCity.push(candidate);
      }
    }

    return candidatesFromCity;
  }

  function getListOfCandidatesLists(response) {

    var xAxis = ['x'];
    var listOfCandidatesLists = [];

    var candidatesList = shuffleArray(response);

    for (var i = 0; i < candidatesList.length; i++) {

      var candidate = candidatesList[i];
      var candidateTwitterId = candidate.target;
      var candidateScoreList = candidate.scores;

      var innerList = [candidateTwitterId];

      for (var j = 0; j < candidateScoreList.length; j++) {

        var scoreNode = candidateScoreList[j];

        if (i === 0) {
          var dateInMillis = scoreNode.date;
          xAxis.push(dateInMillis);
        }

        var scoreValue = scoreNode.value;
        if (!isNaN(scoreValue)) {
          innerList.push(scoreValue);
        }
      }

      listOfCandidatesLists.push(innerList);
    }

    listOfCandidatesLists.unshift(xAxis);
    return listOfCandidatesLists;
  }

  function getAllTargetsScoreSuccess(response) {

    var candidatesFromCity = getCandidatesFromCity($scope.cityId, response);
    var listOfCandidatesLists = getListOfCandidatesLists(candidatesFromCity);
    $scope.chart = getCandidateScore(listOfCandidatesLists);
    $scope.showPopularCandidatesOnly();
  }

  function logError(response) {
    console.error(response);
  }

  $scope.changeMessageBoxState = function() {
    $scope.boxIsFull = !$scope.boxIsFull;
    $scope.showOrHide = $scope.boxIsFull ? 'Ocultar' : "Mostrar";
  };

  $scope.init = function() {

    $scope.cityId = $stateParams.cityId;

    if ($scope.cityId) {
      $scope.boxIsFull = true;
      $scope.showOrHide = 'Ocultar';
      scoreService.getAllTargetsScore(getAllTargetsScoreSuccess, logError);
    } else {
      $state.go('select');
    }
  };
}
