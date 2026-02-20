import axios from 'axios';

const MEALDB_BASE = 'https://www.themealdb.com/api/json/v1/1';

const api = axios.create({
  baseURL: MEALDB_BASE,
});

// Wrapper functions to map TheMealDB responses to what our Frontend expects.

export const getMeals = async () => {
  // Use search with empty string to get a list of meals
  const res = await api.get('/search.php?s=');
  return { data: res.data.meals || [] }; // Landing expects data to be an array
};

export const getMealById = async (id) => {
  const res = await api.get(`/lookup.php?i=${id}`);
  return { data: res.data }; // MealDetail handles `res.data.meals[0]`
};

export const searchMeals = async (query) => {
  const res = await api.get(`/search.php?s=${query}`);
  return { data: res.data.meals || [] }; 
};

export const getLeastIngredientsMeal = async () => {
  // Fake "least ingredients" by getting a random meal
  const res = await api.get('/random.php');
  const meal = res.data.meals ? res.data.meals[0] : null;
  return { data: meal }; // Landing expects res.data to be the meal object directly
};

export const getMealCalories = async (id) => {
  // Mock artificial calorie response 
  await new Promise(resolve => setTimeout(resolve, 600));
  return {
    data: {
      totalCalories: Math.floor(Math.random() * 400) + 300,
      ingredients: [] // fallback to MealDB ingredients
    }
  };
};

export const createMeal = async (mealData) => {
  // Mock POST request 
  await new Promise(resolve => setTimeout(resolve, 1000));
  return { data: { message: "Meal saved successfully (Mocked)" } };
};

export default api;
