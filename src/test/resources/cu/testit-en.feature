#language:en

@qsystem
Feature: qsystem skill

  # Сценарий для изучения.
  @begin
  Scenario: qsc1
    Then qsystem version 20
    When run qsystem