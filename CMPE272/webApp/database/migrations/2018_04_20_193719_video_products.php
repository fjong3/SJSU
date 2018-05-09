<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;
class VideoProducts extends Migration {
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up() {
        $this->createProductsTable();
        $this->addProducts();
    }
    public function createProductsTable() {
        Schema::create('products', function (Blueprint $table) {
            $table->increments('id');
            $table->string('name');
            $table->string('img');
            $table->string('url');
            $table->string('description');
            $table->timestamp('updated_at')->useCurrent();
            $table->timestamp('created_at')->useCurrent();
            $table->string('archived')->nullable()->default(null);
            $table->boolean('service')->default(false);
            $table->unsignedInteger('company_id')->default(0);
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('products');
    }

    public function addProducts() {
        $locations = [
            [
                "name" => "Šolta, Croatia",
                "description" => "The jewel of the Adriatic Sea, Croatia is home to more than 1,200 islands, but travel between them has long been dictated by expensive yacht charters and sluggish public ferries.",
                "img" => "solta-croatia.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Greenville, South Carolina",
                "description" => "Once a sleepy second fiddle to Southern culinary powerhouses like Charleston and Nashville, Greenville is stepping into the limelight with hot new restaurants.",
                "img" => "greenville-southCarolina.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Grenada",
                "description" => "Grenada, known as Spice Island, remains one of the Caribbean’s under-the-radar gems, even though it’s got what every traveler wants: uncrowded beaches, preserved rain forests, and a lively local culture and cuisine.",
                "img" => "grenada.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Buenos Aires, Argentina",
                "description" => "This year, Buenos Aires becomes a hub for art, sports, and politics: the inaugural Art Basel Cities program, the Youth Olympic Games, and the G20 will all take place in the city, beginning with the multi-year Art Basel initiative.",
                "img" => "buenosAires-argentina.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Los Cabos, Mexico",
                "description" => "Located at the tip of the Baja Peninsula, the two small colonial towns of Cabo San Lucas and San José del Cabo have become the hottest vacation destinations in Mexico in recent years. ",
                "img" => "losCabos-mexico.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Walla Walla Valley, Washington",
                "description" => "With more than 300 days of sunshine each year, the southeastern corner of Washington state is home to three flourishing viticultural regions: the Columbia, Walla Walla, and Yakima Valleys.",
                "img" => "wallaWallaValley-washington.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Uzbekistan",
                "description" => "Although the former Soviet republic might seem remote, Uzbekistan once sat at the very center of the world.",
                "img" => "uzbekistan.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Egypt",
                "description" => "Political strife and economic woes have taken a toll on Egypt’s tourism industry in recent years, but travelers will soon have a new reason to visit.",
                "img" => "egypt.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Marrakesh, Morocco",
                "description" => "The Moroccan city has attracted an artistic crowd since the 1960s, when everyone from Yves Saint Laurent to Mick Jagger fell for its vibrant sensory landscape.",
                "img" => "marrakesh-morocco.png",
                "url" => "",
                "company_id" => 1,
            ],
            [
                "name" => "Fiji",
                "description" => "It’s no secret that Fiji is home to some of the world’s most spectacular scenery — powdery beaches fringed with palms, crystalline waters with colorful reefs, and rugged coastlines covered in greenery.",
                "img" => "fiji.png",
                "url" => "",
                "company_id" => 1,
            ]
        ];       
        foreach ($locations as $key=>$location) {
            $id     = DB::table('products')->insertGetId( $location );
            echo sprintf("Added %s to products - %s \n", array_get($location,'name','') , $id);
        }
        
        $Services= [
            [
                "name" => "Consultation",
                "description" => "is one of the service we provided. We will have our experts consult with you in person or by call. 
Contact us to schedule an appointment.",
                "img" => "Consultation.png",
                "url" => "http://fulbertj.com/pages/Consultation.php",
                "company_id" => 3,
            ],
            [
                "name" => "Schematic Design",
                "description" => "is one of the service we provided. We will have our experts design a board schematic according to your design Criteria. Contact us to schedule an appointment.",
                "img" => "SchematicDesign.png",
                "url" => "http://fulbertj.com/pages/SchematicDesign.php",
                "company_id" => 3,
            ],
            [
                "name" => "Schematic Testing",
                "description" => "is one of the service we provided. We will have our experts verify the schematic design that you provide. (Note that schematic testing is included if you are using our design) Contact us to schedule an appointment.",
                "img" => "SchematicTesting.png",
                "url" => "http://fulbertj.com/pages/SchematicTesting.php",
                "company_id" => 3,
            ],
            [
                "name" => "Board Design",
                "description" => "is one of the service we provided. We will have our experts design a board layout based on the schematic diagram. Contact us to schedule an appointment.",
                "img" => "BoardDesign.png",
                "url" => "http://fulbertj.com/pages/BoardDesign.php",
                "company_id" => 3,
            ],
            [
                "name" => "Board Verification",
                "description" => "is one of the service we provided. Before fabrication, We will have our experts verify the board design. This is part of the pre-silicon process. Contact us to schedule an appointment.",
                "img" => "losCabos-mexico.png",
                "url" => "http://fulbertj.com/pages/BoardVerification.php",
                "company_id" => 3,
            ],
            [
                "name" => "Board Fabrication",
                "description" => "is one of the service we provided. Although, we do not have our own manufacture equipment, we do have trusted partners for our fabrication work. We will handle all communication to make sure that your baord is successfully manufactured. 
Contact us to schedule an appointment.",
                "img" => "BoardFabrication.png",
                "url" => "http://fulbertj.com/pages/BoardFabrication.php",
                "company_id" => 3,
            ],
            [
                "name" => "Board Assembly",
                "description" => "is one of the service we provided. Although, we do not have our own assembly department, we do have trusted partners for our assembling board's component. Note that we will provide basic testing along with our assembly service. 
Contact us to schedule an appointment.",
                "img" => "BoardAssembly.png",
                "url" => "http://fulbertj.com/pages/BoardAssembly.php",
                "company_id" => 3,
            ],
            [
                "name" => "Board Testing",
                "description" => "is one of the service we provided. We will have our experts test the PCB board you provided. Contact us to schedule an appointment.",
                "img" => "Board Testing.png",
                "url" => "http://fulbertj.com/pages/BoardTesting.php",
                "company_id" => 3,
            ],
            [
                "name" => "Firmware Design",
                "description" => "is one of the service we provided. We will have our experts (we specialize in ARM based processor) design the firmware for your application. Contact us to schedule an appointment.",
                "img" => "FirmwareDesign.png",
                "url" => "http://fulbertj.com/pages/FirmwareDesign.php",
                "company_id" => 3,
            ],
            [
                "name" => "Firmware Verification",
                "description" => "is one of the service we provided. We will have our experts to verify the Firmware you have developed.
Note that we will automatically verify our firwmare if you use our service to design it. Contact us to schedule an appointment.",
                "img" => "Firmware Verification.png",
                "url" => "http://fulbertj.com/pages/FirmwareVerification.php",
                "company_id" => 3,
            ],
            [
                "name" => "Albuquerque, New Mexico",
                "description" => "Rising above its associations with the annual hot-air-balloon festival, Albuquerque will this year set out to prove itself as a fully-fledged destination.",
                "img" => "albuquerque-newMexico.png",
                "url" => "",
                "company_id" => 3,
            ]

        ];
        foreach ($Services as $key=>$Services ) {
            $id     = DB::table('products')->insertGetId( $Services );
            echo sprintf("Added %s to products - %s \n", array_get($Services,'name','') , $id);
        }
    }

}
