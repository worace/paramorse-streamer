require "json"  # => true
require "http"  # => true

def stream_from(url, &block)
  body = HTTP.get(url).body

  i = 0
  chunk = body.readpartial
  until chunk.to_s.empty?
    yield chunk
    i += 1
    chunk = body.readpartial
  end
end                           # => :stream_from


def letter_mappings
  @letter_mappings ||= JSON.parse(File.read("./resources/morse_code.json"))  # => {" "=>["silence"], "A"=>["dot", "dash"], "B"=>["dash", "dot", "dot", "dot"], "C"=>["dash", "dot", "dash", "dot"], "D"=>["dash", "dot", "dot"], "E"=>["dot"], "F"=>["dot", "dot", "dash", "dot"], "G"=>["dash", "dash", "dot"], "H"=>["dot", "dot", "dot", "dot"], "I"=>["dot", "dot"], "J"=>["dot", "dash", "dash", "dash", "dash"], "K"=>["dash", "dot", "dash"], "L"=>["dot", "dash", "dot", "dot"], "M"=>["dash", "dash"], "N"=>["dash", "dot"], "O"=>["dash", "dash", "dash"], "0"=>["dash", "dash", "dash", "dash", "dash"], "P"=>["dot", "dash", "dash", "dot"], "1"=>["dot", "dash", "dash", "dash", "dash"], "Q"=>["dash", "dash", "dot", "dash"], "2"=>["dot", "dot", "dash", "dash", "dash"], "R"=>["dot", "dash", "dot"], "3"=>["dot", "dot", "dot", "dash", "dash"], "S"=>["dot", "dot", "dot"], "4"=>["dot", "dot", "dot", "dot", "dash", "dash"], "T"=>["dash"], "5"=>["dot", "dot", "dot", "dot", "dot"], "U"=>["dot", "dot", "dash"], "6"=>["dash", "dot", "dot", "dot", "dot"], "V"=>["dot", "dot", "dot", "dash"], "7"=>["dash", "dash", "dot", "dot", "dot"], "W"=>["dot", "dash", "dash"], "8"=>["dash", "dash", "dash", "dot", "dot"], "X"=>["dash", "dot", "dot", "dash"], "9"=>["dash", "dash", "dash", "dash", "dot"], "Y"=>["dash", "dot", "dash", "dash"], "Z"=>["dash", "dash", "dot", "dot"]}
end

def tones_to_bits
  {"dot" => "1", "dash" => "111", "silence" => "0"}
end

def bits_to_letters
  @bits_to_letters ||= letter_mappings.invert.map do |tones, letter|
    tones = tones.map do |t|
      tones_to_bits[t]
    end.join("0")

    [tones, letter]
  end.to_h
end

def read_message
  letters = []
  current_char = ""
  stream_from("http://localhost:9292?chunk_size=16") do |chunk|
    chunk.each_char do |bit|
      if current_char.end_with?("000")
        if current_char == "000" && bit == "0"
          letters << ["0", " "]
          current_char = ""
        else
          letters << [current_char[0..-4], bits_to_letters[current_char[0..-4]]]
          current_char = bit
        end
        print letters.last.last
      else
        current_char += bit
      end
    end
  end
  letters << [current_char, bits_to_letters[current_char]]
  print letters.last.last
end

if __FILE__ == $0
  read_message
end
