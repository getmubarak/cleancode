double getPayAmount() {
  double result;
  if (_isDead) result = deadAmount();
  else {
   //logic
    if (_isSeparated) result = separatedAmount();
    else {
      //logic
      if (_isRetired) result = retiredAmount();
      else result = normalPayAmount();
    };
  }
  return result;
}; 
