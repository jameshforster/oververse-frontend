package models

import specs.UnitSpec

class UserDetailsModelSpec extends UnitSpec {

  "Calling levelToString" should {

    "return a response of Banned" when {

      "provided with a level of -1" in {
        UserDetailsModel("", "", -1).levelToString shouldBe "Banned"
      }
    }

    "return a response of Unverified" when {

      "provided with a level of 0" in {
        UserDetailsModel("", "", 0).levelToString shouldBe "Unverified"
      }

      "provided with a level of 99" in {
        UserDetailsModel("", "", 99).levelToString shouldBe "Unverified"
      }
    }

    "return a response of Verified" when {

      "provided with a level of 100" in {
        UserDetailsModel("", "", 100).levelToString shouldBe "Verified"
      }

      "provided with a level of 199" in {
        UserDetailsModel("", "", 199).levelToString shouldBe "Verified"
      }
    }

    "return a response of Moderator" when {

      "provided with a level of 200" in {
        UserDetailsModel("", "", 200).levelToString shouldBe "Moderator"
      }

      "provided with a level of 499" in {
        UserDetailsModel("", "", 499).levelToString shouldBe "Moderator"
      }
    }

    "return a response of Administrator" when {

      "provided with a level of 500" in {
        UserDetailsModel("", "", 500).levelToString shouldBe "Administrator"
      }

      "provided with a level of 999" in {
        UserDetailsModel("", "", 999).levelToString shouldBe "Administrator"
      }
    }

    "return a response of Owner" when {

      "provided with a level of 1000" in {
        UserDetailsModel("", "", 1000).levelToString shouldBe "Owner"
      }
    }
  }
}
