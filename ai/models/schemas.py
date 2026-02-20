# Data schemas for AI models
from pydantic import BaseModel, Field
from typing import List, Dict


class MealModel(BaseModel):
    name: str = Field(..., description="Meal name")
    calories: int = Field(..., ge=0)
    ingredientCount: int = Field(..., ge=0)


class AIResponseModel(BaseModel):
    type: str = Field(..., description="Detected query type")
    constraints: Dict = Field(default_factory=dict)
    meals: List[MealModel]