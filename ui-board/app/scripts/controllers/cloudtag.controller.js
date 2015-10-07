'use strict';

boardModule.controller('cloudtagController', cloudtagController);
cloudtagController.$inject = ['$scope', 'cloudtagService'];

function cloudtagController($scope, cloudtagService) {
  var popularCandidateWords = [];
  var allWords = [];


  function getCandidateColor(candidateTwitterId) {
    var colors = {
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
    var col = colors[candidateTwitterId];
    return col;
  }

  function scale(value, min, max){
    var newValue = 1 + (((value - min)*(10 - 1))/(max-min));
    return Math.ceil(newValue);
  }

  function shuffleArray(o) {
    for(var j, x, i = o.length; i; j = Math.floor(Math.random() * i), x = o[--i], o[i] = o[j], o[j] = x);
    return o;
  }

  function success(response) {
    var minSize = 1000000;
    var maxSize = 0;
    var candidatosNoPopulares = ['CVderoux', 'MMMaldonadoC', 'RicardoAriasM', 'AlexVernot', 'danielraisbeck'];

    for(var i = 0 ; i < response.length ; i++){
      var element = response[i];
      var candidateList = element.wordCountList;
      for(var j = 0 ; j < candidateList.length ; j++){
        var wordCount = candidateList[j];
        var word = {
          word: ' '+wordCount.word+' ',
          size: wordCount.count,
          color: getCandidateColor(element.twitterId)
        };
        minSize = Math.min(minSize, wordCount.count);
        maxSize = Math.max(maxSize, wordCount.count);
        allWords.push(word);
        if(candidatosNoPopulares.indexOf(element.twitterId) === -1){
          popularCandidateWords.push(word);
        }
      }
    }

    for(var i = 0 ; i < popularCandidateWords.length ; i++){
      var w = popularCandidateWords[i];
      w.size = scale(w.size, minSize, maxSize);
    }

    $scope.words = shuffleArray(popularCandidateWords);
  }

  $scope.showAllCandidates = function() {
    $scope.words = shuffleArray(allWords);
    //$scope.$apply()
  };

  $scope.showPopularCandidatesOnly = function() {
    $scope.words = shuffleArray(popularCandidateWords);
    //$scope.$apply()
  };

  function error(response) {
    console.error(response);
  }

  $scope.changeMessageBoxState = function() {
    $scope.boxIsFull = !$scope.boxIsFull;
    $scope.showOrHide = $scope.boxIsFull ? 'Ocultar' : "Mostrar";
  };

  $scope.init = function() {
    $scope.boxIsFull = true;
    $scope.showOrHide = 'Ocultar';
    cloudtagService.getAllCandidatesCloudTags(success, error);
  };
}
